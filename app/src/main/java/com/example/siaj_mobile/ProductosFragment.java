package com.example.siaj_mobile;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siaj_mobile.adapters.ProductAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductosFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private RecyclerView inventoryRecyclerView;
    private ProductAdapter productAdapter;
    private List<Producto> products;
    private List<Producto> filteredProducts;
    private TextInputEditText searchEditText;
    private Button addButton;
    private static OkHttpClient client = new OkHttpClient();
    private static Gson gson = new Gson();
    private static final String BASE_URL = VariablesEntorno.getServerURL();
    private static final String TAG = "ProductosFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_productos, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearchFunctionality();
        setupClickListeners();
        loadProducts();

        return view;
    }

    private void initViews(View view) {
        inventoryRecyclerView = view.findViewById(R.id.inventoryRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        // addButton = view.findViewById(R.id.addButton);
    }

    private void setupRecyclerView() {
        products = new ArrayList<>();
        filteredProducts = new ArrayList<>();

        productAdapter = new ProductAdapter(getContext(), filteredProducts);
        productAdapter.setOnProductClickListener(this);

        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        inventoryRecyclerView.setAdapter(productAdapter);
    }

    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // no-op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // no-op
            }
        });
    }

    private void setupClickListeners() {
        // addButton.setOnClickListener(v -> {
        //     Toast.makeText(getContext(), "Agregar producto (lógica no implementada)", Toast.LENGTH_SHORT).show();
        // });
    }

    private void loadProducts() {
        products.clear();

        getProductosFromServer(getContext(), new ProductosCallback() {
            @Override
            public void onSuccess(List<Producto> productos) {
                products.addAll(productos);
                filterProducts("");
            }

            @Override
            public void onError(String error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error cargando productos: " + error, Toast.LENGTH_LONG).show();
                }
                Log.e(TAG, "Error cargando productos: " + error);
            }
        });
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        if (query.isEmpty()) {
            filteredProducts.addAll(products);
        } else {
            for (Producto producto : products) {
                if (producto.getNombre().toLowerCase().contains(query.toLowerCase())) {
                    filteredProducts.add(producto);
                }
            }
        }
        productAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewClick(Producto product) {
        Toast.makeText(getContext(), "Ver: " + product.getNombre(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(Producto product) {
        Toast.makeText(getContext(), "Editar: " + product.getNombre(), Toast.LENGTH_SHORT).show();
    }

    // Interfaz y método para obtener productos (igual que antes)
    public interface ProductosCallback {
        void onSuccess(List<Producto> productos);
        void onError(String error);
    }

    public static void getProductosFromServer(Context context, ProductosCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/productos")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error de conexión con servidor", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onError("Sin conexión al servidor");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = null;
                try {
                    if (response.body() != null) {
                        responseBody = response.body().string();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error leyendo el cuerpo de la respuesta", e);
                }

                Log.d(TAG, "Código HTTP: " + response.code());
                Log.d(TAG, "Cuerpo de la respuesta: " + responseBody);

                if (response.isSuccessful() && responseBody != null) {
                    try {
                        Type listType = new TypeToken<List<Producto>>(){}.getType();
                        List<Producto> productos = gson.fromJson(responseBody, listType);

                        new Handler(Looper.getMainLooper()).post(() -> {
                            callback.onSuccess(productos);
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "Error parseando productos JSON", e);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            callback.onError("Error procesando datos del servidor");
                        });
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onError("Error del servidor: " + response.code());
                    });
                }

                if (response.body() != null) {
                    response.close();
                }
            }
        });
    }
}