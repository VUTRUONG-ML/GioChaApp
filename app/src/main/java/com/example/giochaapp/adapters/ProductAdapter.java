package com.example.giochaapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.giochaapp.R;
import com.example.giochaapp.models.Product;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
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
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName;
        private TextView productDescription;
        private TextView productRating;
        private TextView originalPrice;
        private TextView productPrice;
        private TextView discountBadge;
        private ImageButton addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productRating = itemView.findViewById(R.id.product_rating);
            originalPrice = itemView.findViewById(R.id.original_price);
            productPrice = itemView.findViewById(R.id.product_price);
            discountBadge = itemView.findViewById(R.id.discount_badge);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }

        public void bind(Product product) {
            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            productRating.setText(String.valueOf(product.getRating()));

            // Load image using Glide
            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.product_placeholder)
                    .into(productImage);

            // Handle discount
            if (product.hasDiscount()) {
                discountBadge.setVisibility(View.VISIBLE);
                discountBadge.setText("-" + product.getDiscount() + "%");

                originalPrice.setVisibility(View.VISIBLE);
                originalPrice.setText(product.getFormattedPrice());

                productPrice.setText(product.getFormattedDiscountedPrice());
            } else {
                discountBadge.setVisibility(View.GONE);
                originalPrice.setVisibility(View.GONE);
                productPrice.setText(product.getFormattedPrice());
            }

            // Click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });

            addToCartButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCartClick(product);
                }
            });
        }

    }
}