package com.example.siaj_mobile.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.siaj_mobile.R;
import com.example.siaj_mobile.Producto;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Producto> products;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onViewClick(Producto product);
        void onEditClick(Producto product);
    }

    public ProductAdapter(Context context, List<Producto> products) {
        this.context = context;
        this.products = products;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Producto product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Producto> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView productNameText;
        private TextView stockStatusText;
        private TextView skuText;
        private TextView supplierText;
        private TextView stockText;
        private TextView priceText;
        private Button viewButton;
        private Button editButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameText = itemView.findViewById(R.id.productNameText);
            stockStatusText = itemView.findViewById(R.id.stockStatusText);
            skuText = itemView.findViewById(R.id.skuText);
            supplierText = itemView.findViewById(R.id.supplierText);
            stockText = itemView.findViewById(R.id.stockText);
            priceText = itemView.findViewById(R.id.priceText);
//            viewButton = itemView.findViewById(R.id.viewButton);
//            editButton = itemView.findViewById(R.id.editButton);
        }

        public void bind(Producto product) {
            productNameText.setText(product.getNombre());
            skuText.setText(context.getString(R.string.sku_format, product.getSku()));
            supplierText.setText(context.getString(R.string.supplier_format, product.getProveedor()));
            stockText.setText(context.getString(R.string.stock_format, product.getStock()));
            priceText.setText(context.getString(R.string.price_format, product.getPrecio()));

            // Set status based on stock
            setStockStatus(product.getStock());

            // Set click listeners
//            viewButton.setOnClickListener(v -> {
//                if (listener != null) {
//                    listener.onViewClick(product);
//                }
//            });
//
//            editButton.setOnClickListener(v -> {
//                if (listener != null) {
//                    listener.onEditClick(product);
//                }
//            });
        }

        private void setStockStatus(int stock) {
            String statusText;
            int statusColor;

            if (stock == 0) {
                statusText = context.getString(R.string.out_of_stock);
                statusColor = ContextCompat.getColor(context, R.color.status_out_of_stock);
            } else if (stock <= 10) {
                statusText = context.getString(R.string.low_stock);
                statusColor = ContextCompat.getColor(context, R.color.status_low_stock);
            } else {
                statusText = context.getString(R.string.in_stock);
                statusColor = ContextCompat.getColor(context, R.color.status_in_stock);
            }

            stockStatusText.setText(statusText);
            stockStatusText.getBackground().setTint(statusColor);
        }
    }
}
