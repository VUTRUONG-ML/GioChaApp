package com.example.giochaapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.giochaapp.R;
import com.example.giochaapp.adapters.OrderDetailItemAdapter;
import com.example.giochaapp.models.CartItem;
import com.example.giochaapp.models.Order;
import com.example.giochaapp.utils.DatabaseHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView orderIdText, orderDateText, orderStatusText,
            deliveryAddressText, phoneText,
            subtotalText, shippingFeeText, totalText, estimatedDeliveryText;
    private LinearLayout estimatedLayout;
    private RecyclerView itemsRecyclerView;
    private ImageButton backButton;
    private ImageView statusBadge;

    private DatabaseHelper dbHelper;
    private Order order;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        dbHelper = new DatabaseHelper(this);

        initViews();
        loadOrder();
    }

    private void initViews() {
        orderIdText = findViewById(R.id.order_id);
        orderDateText = findViewById(R.id.order_date);
        orderStatusText = findViewById(R.id.order_status);
        deliveryAddressText = findViewById(R.id.delivery_address);
        phoneText = findViewById(R.id.phone);
        subtotalText = findViewById(R.id.subtotal);
        shippingFeeText = findViewById(R.id.shipping_fee);
        totalText = findViewById(R.id.total_amount);
        estimatedDeliveryText = findViewById(R.id.estimated_delivery_text);
        estimatedLayout = findViewById(R.id.estimated_layout);
        itemsRecyclerView = findViewById(R.id.items_recycler);
        backButton = findViewById(R.id.back_button);
        statusBadge = findViewById(R.id.status_badge);

        backButton.setOnClickListener(v -> finish());
    }

    private void loadOrder() {
        String orderId = getIntent().getStringExtra("order_id");
        if (orderId == null) {
            finish();
            return;
        }

        order = dbHelper.getOrderById(orderId);
        if (order == null) {
            Log.e("OrderDetail", "Không tìm thấy đơn hàng với ID: " + orderId);
            finish();
            return;
        }

        List<CartItem> items = dbHelper.getOrderItems(orderId);
        if (items == null || items.isEmpty()) {
            Log.e("OrderDetail", "Không có sản phẩm nào trong đơn hàng " + orderId);
        }

        order.setItems(items);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        orderIdText.setText("Đơn hàng #" + order.getId());
        orderDateText.setText(sdf.format(order.getOrderDate()));
        orderStatusText.setText(order.getStatus());
        deliveryAddressText.setText(order.getDeliveryAddress());
        phoneText.setText(order.getPhone());
        subtotalText.setText(String.format("%,d ₫", order.getTotalPrice()));
        shippingFeeText.setText("Miễn phí");
        totalText.setText(String.format("%,d ₫", order.getTotalPrice()));

        if ("Đang xử lý".equals(order.getStatus())) {
            estimatedLayout.setVisibility(View.VISIBLE);
        } else {
            estimatedLayout.setVisibility(View.GONE);
        }

        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsRecyclerView.setAdapter(new OrderDetailItemAdapter(this, items));
    }
}