package com.example.giochaapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.giochaapp.R;
import com.example.giochaapp.models.Order;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private SimpleDateFormat dateFormat;
    private OnOrderClickListener listener;

    // Interface để bắt sự kiện click
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderHistoryAdapter(Context context, List<Order> orderList, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orderList.get(position));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView orderIdText, orderDateText, orderStatusText, orderTotalText, itemCountText;
        private TextView viewDetailsButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.order_id);
            orderDateText = itemView.findViewById(R.id.order_date);
            orderStatusText = itemView.findViewById(R.id.order_status);
            orderTotalText = itemView.findViewById(R.id.order_total);
            itemCountText = itemView.findViewById(R.id.order_item_count);
            viewDetailsButton = itemView.findViewById(R.id.view_details_button);
        }

        public void bind(Order order) {
            orderIdText.setText("Đơn hàng #" + order.getId());
            orderDateText.setText(dateFormat.format(order.getOrderDate()));
            orderStatusText.setText(order.getStatus());
            orderTotalText.setText(order.getFormattedTotalPrice());

            if (order.getItems() != null) {
                itemCountText.setText(order.getItemCount() + " sản phẩm");
            } else {
                itemCountText.setText("0 sản phẩm");
            }

            // Đổi màu trạng thái
            int statusColor = getStatusColor(order.getStatus());
            orderStatusText.setTextColor(statusColor);

            // Sự kiện click "Xem chi tiết"
            viewDetailsButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(order);
                }
            });
        }

        private int getStatusColor(String status) {
            switch (status) {
                case "Đã giao":
                    return context.getResources().getColor(R.color.status_delivered);
                case "Đang giao":
                    return context.getResources().getColor(R.color.status_shipping);
                case "Đang xử lý":
                    return context.getResources().getColor(R.color.status_preparing);
                default:
                    return context.getResources().getColor(R.color.status_pending);
            }
        }
    }
}