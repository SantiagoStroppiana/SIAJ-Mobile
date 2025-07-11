package com.example.siaj_mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siaj_mobile.R;
import com.example.siaj_mobile.VentaDTO;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class VentaAdapter extends RecyclerView.Adapter<VentaAdapter.VentaViewHolder> {

    private Context context;
    private List<VentaDTO> ventaList;

    public VentaAdapter(Context context, List<VentaDTO> ventaList) {
        this.context = context;
        this.ventaList = ventaList;
    }

    @NonNull
    @Override
    public VentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_venta, parent, false);
        return new VentaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VentaViewHolder holder, int position) {
        VentaDTO venta = ventaList.get(position);

        holder.tvUsuario.setText(venta.getUsuarioDTO().getNombre());
        holder.tvTotal.setText(NumberFormat.getCurrencyInstance(new Locale("es", "AR")).format(venta.getTotal()));
        holder.tvEstado.setText(venta.getEstado());
        holder.tvFecha.setText(formatearFecha(venta.getFechaPago()));

        holder.tvMedioPago.setText(venta.getMedioPago().getTipo());
    }
    private String formatearFecha(String fechaOriginal) {
        try {
            // El formato original es tipo: "2025-07-11T14:40:14"
            java.time.LocalDateTime fecha = java.time.LocalDateTime.parse(fechaOriginal);
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return fecha.format(formatter);
        } catch (Exception e) {
            return fechaOriginal; // Si falla el parseo, muestra la original
        }
    }


    @Override
    public int getItemCount() {
        return ventaList.size();
    }

    public static class VentaViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsuario, tvTotal, tvEstado, tvFecha, tvMedioPago;

        public VentaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsuario = itemView.findViewById(R.id.tvUsuarioVenta);
            tvTotal = itemView.findViewById(R.id.tvTotalVenta);
            tvEstado = itemView.findViewById(R.id.tvEstadoVenta);
            tvFecha = itemView.findViewById(R.id.tvFechaVenta);
            tvMedioPago = itemView.findViewById(R.id.tvMedioPagoVenta);
        }
    }
}
