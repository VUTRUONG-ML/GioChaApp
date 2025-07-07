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
import com.example.giochaapp.models.CartItem;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnCartItemListener listener;

    public interface OnCartItemListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onItemRemoved(CartItem item);
    }

    public CartAdapter(Context context) {
        this.context = context;
        this.cartItems = new ArrayList<>();
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    public void setOnCartItemListener(OnCartItemListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView cartItemImage;
        private TextView cartItemName;
        private TextView cartItemPrice;
        private ImageButton decreaseQuantity;
        private ImageButton increaseQuantity;
        private TextView cartItemQuantity;
        private TextView cartItemTotal;
        private ImageButton removeItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cartItemImage = itemView.findViewById(R.id.cart_item_image);
            cartItemName = itemView.findViewById(R.id.cart_item_name);
            cartItemPrice = itemView.findViewById(R.id.cart_item_price);
            decreaseQuantity = itemView.findViewById(R.id.decrease_quantity);
            increaseQuantity = itemView.findViewById(R.id.increase_quantity);
            cartItemQuantity = itemView.findViewById(R.id.cart_item_quantity);
            cartItemTotal = itemView.findViewById(R.id.cart_item_total);
            removeItem = itemView.findViewById(R.id.remove_item);
        }

        public void bind(CartItem item) {
            cartItemName.setText(item.getName());
            cartItemQuantity.setText(String.valueOf(item.getQuantity()));
            cartItemTotal.setText(item.getFormattedTotalPrice());

            // Show price with discount if applicable
            if (item.hasDiscount()) {
                cartItemPrice.setText(item.getFormattedDiscountedPrice());
            } else {
                cartItemPrice.setText(item.getFormattedPrice());
            }

            // Load image
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.product_placeholder)
                    .into(cartItemImage);

            // Setup click listeners
            decreaseQuantity.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() - 1;
                if (newQuantity > 0 && listener != null) {
                    item.setQuantity(newQuantity);
                    cartItemQuantity.setText(String.valueOf(newQuantity));
                    cartItemTotal.setText(item.getFormattedTotalPrice());
                    listener.onQuantityChanged(item, newQuantity);
                }
            });

            increaseQuantity.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() + 1;
                if (listener != null) {
                    item.setQuantity(newQuantity);
                    cartItemQuantity.setText(String.valueOf(newQuantity));
                    cartItemTotal.setText(item.getFormattedTotalPrice());
                    listener.onQuantityChanged(item, newQuantity);
                }
            });

            removeItem.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemRemoved(item);
                }
            });
        }
    }
}