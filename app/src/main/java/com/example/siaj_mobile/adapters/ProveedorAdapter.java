package com.example.siaj_mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siaj_mobile.Proveedor;
import com.example.siaj_mobile.R;

import java.util.List;

public class ProveedorAdapter extends RecyclerView.Adapter<ProveedorAdapter.ProveedorViewHolder> {

    private Context context;
    private List<Proveedor> proveedorList;
    private OnProveedorClickListener clickListener;

    public ProveedorAdapter(Context context, List<Proveedor> proveedorList) {
        this.context = context;
        this.proveedorList = proveedorList;
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

//        holder.btnVer.setOnClickListener(v -> {
//            if (clickListener != null) {
//                clickListener.onViewClick(proveedor);
//            }
//        });
//
//        holder.btnEditar.setOnClickListener(v -> {
//            if (clickListener != null) {
//                clickListener.onEditClick(proveedor);
//            }
//        });
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
        TextView nombreTextView, emailTextView;
        ImageButton btnVer, btnEditar;

        public ProveedorViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.proveedorNombre);
            emailTextView = itemView.findViewById(R.id.proveedorEmail);
//            btnVer = itemView.findViewById(R.id.btnVerProveedor);
//            btnEditar = itemView.findViewById(R.id.btnEditarProveedor);
        }
    }
}
