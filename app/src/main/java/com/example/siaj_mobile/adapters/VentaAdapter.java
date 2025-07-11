package com.example.siaj_mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siaj_mobile.R;
import com.example.siaj_mobile.VentaDTO;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class VentaAdapter extends RecyclerView.Adapter<VentaAdapter.VentaViewHolder> {

    private Context context;
    private List<VentaDTO> ventaList;
    private OnVentaDetallesClickListener detallesClickListener;

    public VentaAdapter(Context context, List<VentaDTO> ventaList) {
        this.context = context;
        this.ventaList = ventaList;
    }

    // Constructor con listener (para usar con el fragment)
    public VentaAdapter(Context context, List<VentaDTO> ventaList, OnVentaDetallesClickListener listener) {
        this.context = context;
        this.ventaList = ventaList;
        this.detallesClickListener = listener;
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
        holder.tvTotal.setText(formatearMoneda(venta.getTotal()));
        holder.tvEstado.setText(venta.getEstado());
        holder.tvFecha.setText(formatearFecha(venta.getFechaPago()));
        holder.tvMedioPago.setText(venta.getMedioPago().getTipo());

        holder.btnVerDetalles.setOnClickListener(v -> {
            if (detallesClickListener != null) {
                detallesClickListener.onDetallesClick(venta);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ventaList.size();
    }

    public void setOnVentaDetallesClickListener(OnVentaDetallesClickListener listener) {
        this.detallesClickListener = listener;
    }

    public interface OnVentaDetallesClickListener {
        void onDetallesClick(VentaDTO venta);
    }

    public static class VentaViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsuario, tvTotal, tvEstado, tvFecha, tvMedioPago;
        Button btnVerDetalles;

        public VentaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsuario = itemView.findViewById(R.id.tvUsuarioVenta);
            tvTotal = itemView.findViewById(R.id.tvTotalVenta);
            tvEstado = itemView.findViewById(R.id.tvEstadoVenta);
            tvFecha = itemView.findViewById(R.id.tvFechaVenta);
            tvMedioPago = itemView.findViewById(R.id.tvMedioPagoVenta);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
        }
    }

    private String formatearFecha(String fechaOriginal) {
        try {
            LocalDateTime fecha = LocalDateTime.parse(fechaOriginal);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return fecha.format(formatter);
        } catch (Exception e) {
            return fechaOriginal;
        }
    }

    private String formatearMoneda(java.math.BigDecimal valor) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
        return formato.format(valor);
    }
}