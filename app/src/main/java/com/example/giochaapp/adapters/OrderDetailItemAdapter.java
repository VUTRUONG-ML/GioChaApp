package com.example.giochaapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.giochaapp.R;
import com.example.giochaapp.models.CartItem;

import java.util.List;

public class OrderDetailItemAdapter extends RecyclerView.Adapter<OrderDetailItemAdapter.ViewHolder> {

    private Context context;
    private List<CartItem> itemList;

    public OrderDetailItemAdapter(Context context, List<CartItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public OrderDetailItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailItemAdapter.ViewHolder holder, int position) {
        CartItem item = itemList.get(position);

        holder.name.setText(item.getName());
        holder.quantity.setText("Số lượng: " + item.getQuantity());
        holder.unitPrice.setText(String.format("%,d ₫", item.getPrice()));
        holder.totalPrice.setText(String.format("%,d ₫", item.getTotalPrice()));

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.product_placeholder)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, quantity, unitPrice, totalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.product_image);
            name = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.product_quantity);
            unitPrice = itemView.findViewById(R.id.product_unit_price);
            totalPrice = itemView.findViewById(R.id.product_total_price);
        }
    }
}