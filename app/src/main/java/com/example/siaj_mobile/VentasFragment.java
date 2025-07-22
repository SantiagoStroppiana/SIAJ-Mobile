package com.example.siaj_mobile;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siaj_mobile.adapters.VentaAdapter;
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

public class VentasFragment extends Fragment {

    private RecyclerView recyclerView;
    private VentaAdapter adapter;
    private List<VentaDTO> ventas = new ArrayList<>();
    private List<VentaDTO> ventasFiltradas = new ArrayList<>();
    private TextView tvVentasHoy;
    private TextInputEditText searchEditText;

    private static final String BASE_URL = VariablesEntorno.getServerURL();
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final String TAG = "VentasFragment";

    // Cache para productos y detalles
    private static List<Producto> productosCache = new ArrayList<>();
    private static List<DetalleVentaDTO> detallesCache = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ventas, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewVentas);
        searchEditText = view.findViewById(R.id.searchEditTextVentas);
        tvVentasHoy = view.findViewById(R.id.tvVentasHoy);
        setupRecyclerView();
        setupSearch();
        loadVentas();
        cargarProductosEnCache();
        cargarDetallesEnCache();


        return view;
    }

    private void TotalVentasHoy() {
        double totalVentas=0;
        for (VentaDTO venta : ventas) {
            totalVentas=totalVentas+venta.getTotal().doubleValue();
        }
        String textoTotal=String.format("%.2f",totalVentas);
        //return textoTotal;
        tvVentasHoy.setText(textoTotal);
    }

    private void setupRecyclerView() {
        adapter = new VentaAdapter(getContext(), ventasFiltradas, venta -> mostrarDialogoDetalles(venta.getId()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVentas(s.toString());
            }
        });
    }

    private void filterVentas(String query) {
        ventasFiltradas.clear();
        if (query.isEmpty()) {
            ventasFiltradas.addAll(ventas);
        } else {
            for (VentaDTO venta : ventas) {
                if (venta.getUsuarioDTO().getNombre().toLowerCase().contains(query.toLowerCase())) {
                    ventasFiltradas.add(venta);
                }
            }
        }
        adapter.notifyDataSetChanged();
        TotalVentasHoy();
    }

    private void cargarDetallesEnCache() {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/detalle-ventas")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error cargando detalles de ventas", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Type listType = new TypeToken<List<DetalleVentaDTO>>(){}.getType();
                    List<DetalleVentaDTO> detalles = gson.fromJson(json, listType);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        detallesCache.clear();
                        detallesCache.addAll(detalles);
                        Log.d(TAG, "Detalles cargados: " + detalles.size());
                    });
                }
            }
        });
    }

    private void cargarProductosEnCache() {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/productos")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error cargando productos", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Type listType = new TypeToken<List<Producto>>(){}.getType();
                    List<Producto> productos = gson.fromJson(json, listType);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        productosCache.clear();
                        productosCache.addAll(productos);
                        Log.d(TAG, "Productos cargados: " + productos.size());
                    });
                }
            }
        });
    }

    private Producto buscarProductoPorId(int id) {
        for (Producto p : productosCache) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    private List<DetalleVentaDTO> obtenerDetallesPorVentaId(int ventaId) {
        List<DetalleVentaDTO> detallesVenta = new ArrayList<>();
        for (DetalleVentaDTO detalle : detallesCache) {
            if (detalle.getVentaId() == ventaId) {
                detallesVenta.add(detalle);
            }
        }
        return detallesVenta;
    }

    private void mostrarDialogoDetalles(int ventaId) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_detalles_venta, null);

        TextView titulo = view.findViewById(R.id.tituloVenta);
        TextView detallesText = view.findViewById(R.id.detallesText);
        Button btnCerrar = view.findViewById(R.id.btnCerrar);

        titulo.setText("🧾 Detalles de Venta #" + ventaId);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        // Lógica para cargar los datos
        List<DetalleVentaDTO> detallesVenta = obtenerDetallesPorVentaId(ventaId);
        if (detallesVenta.isEmpty()) {
            detallesText.setText("No se encontraron detalles para esta venta.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        double totalVenta = 0;

        for (DetalleVentaDTO detalle : detallesVenta) {
            Producto producto = buscarProductoPorId(detalle.getProductoId());
            String nombreProducto = producto != null ? producto.getNombre() : "Producto ID " + detalle.getProductoId();
            double subtotal = detalle.getCantidad() * detalle.getPrecioUnitario();
            totalVenta += subtotal;

            sb.append("• ").append(nombreProducto)
                    .append("\n   Cantidad: ").append(detalle.getCantidad())
                    .append(" x $").append(String.format("%.2f", detalle.getPrecioUnitario()))
                    .append(" = $").append(String.format("%.2f", subtotal))
                    .append("\n\n");
        }

        sb.append("💰 TOTAL: $").append(String.format("%.2f", totalVenta));
        detallesText.setText(sb.toString());
    }






    private void loadVentas() {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/ventas")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    try {
                        Type listType = new TypeToken<List<VentaDTO>>() {}.getType();
                        List<VentaDTO> lista = gson.fromJson(body, listType);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            ventas.clear();
                            ventas.addAll(lista);
                            filterVentas("");
                            TotalVentasHoy();
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Error parseando ventas", e);
                    }
                }
            }
        });
    }
}