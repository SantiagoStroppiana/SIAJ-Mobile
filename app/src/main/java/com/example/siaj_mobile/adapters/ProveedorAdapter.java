package com.example.siaj_mobile.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siaj_mobile.Proveedor;
import com.example.siaj_mobile.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ProveedorAdapter extends RecyclerView.Adapter<ProveedorAdapter.ProveedorViewHolder> {

    private Context context;
    private List<Proveedor> proveedorList;
    private OnProveedorClickListener clickListener;
    private OnPermissionRequestListener permissionRequestListener;

    public interface OnPermissionRequestListener {
        void onRequestCallPermission();
    }


    public ProveedorAdapter(Context context, List<Proveedor> proveedorList) {
        this.context = context;
        this.proveedorList = proveedorList;
    }

    public void setOnPermissionRequestListener(OnPermissionRequestListener listener){
        this.permissionRequestListener = listener;
    }



    @NonNull
    @Override
    public ProveedorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_proveedor, parent, false);
        return new ProveedorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProveedorViewHolder holder, int position) {
        Proveedor proveedor = proveedorList.get(position);
        holder.nombreTextView.setText(proveedor.getRazonSocial());
        holder.emailTextView.setText(proveedor.getEmail());
        holder.telefonoTextView.setText(proveedor.getTelefono());

        holder.btnLlamarProveedor.setOnClickListener(v -> {
            String telefonoProveedor = proveedor.getTelefono();
            if (telefonoProveedor != null &&  !telefonoProveedor.isEmpty()){
                String numeroLimpio = limpiarNumeroTelefono(telefonoProveedor);
                String phoneNumber = "tel:" + numeroLimpio;
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    context.startActivity(intent);
                } else {
                    if(permissionRequestListener != null){
                        permissionRequestListener.onRequestCallPermission();
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return proveedorList.size();
    }

    public void setOnProveedorClickListener(OnProveedorClickListener listener) {
        this.clickListener = listener;
    }

    public interface OnProveedorClickListener {
        void onViewClick(Proveedor proveedor);
        void onEditClick(Proveedor proveedor);
    }

    public static class ProveedorViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView, emailTextView, telefonoTextView;
        MaterialButton btnLlamarProveedor;

        public ProveedorViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.proveedorNombre);
            emailTextView = itemView.findViewById(R.id.proveedorEmail);
            telefonoTextView = itemView.findViewById(R.id.proveedorTelefono);
            btnLlamarProveedor = itemView.findViewById(R.id.btnLlamarProveedor);

        }
    }

    private String limpiarNumeroTelefono(String numero) {
        // Eliminar espacios, guiones, paréntesis
        String limpio = numero.replaceAll("[\\s\\-\\(\\)]", "");

        // Asegurar que empiece con +54 si es número argentino
        if (!limpio.startsWith("+")) {
            if (limpio.startsWith("54")) {
                limpio = "+" + limpio;
            } else {
                limpio = "+54" + limpio;
            }
        }

        return limpio;
    }
}
