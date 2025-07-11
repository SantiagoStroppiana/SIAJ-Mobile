package com.example.siaj_mobile;

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
    private TextInputEditText searchEditText;

    private static final String BASE_URL = VariablesEntorno.getServerURL();
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final String TAG = "VentasFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ventas, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewVentas);
        searchEditText = view.findViewById(R.id.searchEditTextVentas);

        setupRecyclerView();
        setupSearch();
        loadVentas();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new VentaAdapter(getContext(), ventasFiltradas);
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
    }

    private void loadVentas() {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/ventas")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    try {
                        Type listType = new TypeToken<List<VentaDTO>>() {}.getType();
                        List<VentaDTO> lista = gson.fromJson(body, listType);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            ventas.clear();
                            ventas.addAll(lista);
                            filterVentas("");
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Error parseando ventas", e);
                    }
                }
            }
        });
    }
}
