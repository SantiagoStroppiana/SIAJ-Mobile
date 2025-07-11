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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siaj_mobile.adapters.ProveedorAdapter;
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

public class ProveedoresFragment extends Fragment implements ProveedorAdapter.OnProveedorClickListener {

    private RecyclerView recyclerView;
    private ProveedorAdapter adapter;
    private List<Proveedor> proveedores;
    private List<Proveedor> proveedoresFiltrados;
    private TextInputEditText searchEditText;
    private static final String BASE_URL = VariablesEntorno.getServerURL();
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final String TAG = "ProveedoresFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proveedores, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewProveedores);
        searchEditText = view.findViewById(R.id.searchEditTextProveedores);

        setupRecyclerView();
        setupSearch();
        loadProveedores();

        return view;
    }

    private void setupRecyclerView() {
        proveedores = new ArrayList<>();
        proveedoresFiltrados = new ArrayList<>();

        adapter = new ProveedorAdapter(getContext(), proveedoresFiltrados);
        adapter.setOnProveedorClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarProveedores(s.toString());
            }
        });
    }

    private void filtrarProveedores(String query) {
        proveedoresFiltrados.clear();
        if (query.isEmpty()) {
            proveedoresFiltrados.addAll(proveedores);
        } else {
            for (Proveedor p : proveedores) {
                if (p.getRazonSocial().toLowerCase().contains(query.toLowerCase())) {
                    proveedoresFiltrados.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadProveedores() {
        proveedores.clear();

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/proveedores")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Fallo de red", e);
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getContext(), "Error de red", Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = null;
                try {
                    if (response.body() != null) {
                        responseBody = response.body().string();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error leyendo la respuesta", e);
                }

                Log.d(TAG, "HTTP " + response.code());
                Log.d(TAG, "Cuerpo: " + responseBody);

                if (response.isSuccessful() && responseBody != null) {
                    try {
                        Type listType = new TypeToken<List<Proveedor>>(){}.getType();
                        List<Proveedor> lista = gson.fromJson(responseBody, listType);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            proveedores.addAll(lista);
                            filtrarProveedores("");
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Error parseando JSON", e);
                        new Handler(Looper.getMainLooper()).post(() ->
                                Toast.makeText(getContext(), "Error al procesar los datos", Toast.LENGTH_LONG).show()
                        );
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(getContext(), "Error del servidor: " + response.code(), Toast.LENGTH_LONG).show()
                    );
                }

                if (response.body() != null) {
                    response.close();
                }
            }
        });
    }

    @Override
    public void onViewClick(Proveedor proveedor) {
        Toast.makeText(getContext(), "Ver: " + proveedor.getRazonSocial(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(Proveedor proveedor) {
        Toast.makeText(getContext(), "Editar: " + proveedor.getRazonSocial(), Toast.LENGTH_SHORT).show();
    }
}
