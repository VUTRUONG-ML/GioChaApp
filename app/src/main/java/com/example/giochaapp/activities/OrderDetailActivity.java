package com.example.giochaapp.activities;

import android.os.AsyncTask;
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
import com.example.giochaapp.config.ApiConfig;
import com.example.giochaapp.models.CartItem;
import com.example.giochaapp.models.Order;
import com.example.giochaapp.utils.DatabaseHelper;
import com.example.giochaapp.utils.HttpHelper;
import com.example.giochaapp.utils.SharedPrefsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        new GetOrderDetailTask().execute(orderId);
    }

    private void updateUI() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        orderIdText.setText("Đơn hàng #" + order.getId());
        orderDateText.setText(sdf.format(order.getOrderDate()));
        orderStatusText.setText(order.getStatus());
        deliveryAddressText.setText(order.getDeliveryAddress());

        SharedPrefsManager prefs = new SharedPrefsManager(OrderDetailActivity.this);
        phoneText.setText(prefs.getUserPhone());
        subtotalText.setText(String.format("%,d ₫", order.getTotalPrice()));
        shippingFeeText.setText("Miễn phí");
        totalText.setText(String.format("%,d ₫", order.getTotalPrice()));

        if ("Đang xử lý".equals(order.getStatus())) {
            estimatedLayout.setVisibility(View.VISIBLE);
        } else {
            estimatedLayout.setVisibility(View.GONE);
        }

        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsRecyclerView.setAdapter(new OrderDetailItemAdapter(this, order.getItems()));
    }


    private class GetOrderDetailTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String orderId = params[0];
            SharedPrefsManager prefs = new SharedPrefsManager(OrderDetailActivity.this);
            String token = prefs.getToken();

            try {
                String url = ApiConfig.BASE_URL + "/api/orders/" + orderId;
                JSONObject response = HttpHelper.getJson(url, token);

                Log.d("OrderDetail", "Response: " + response); // In response để kiểm tra
                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (response == null) {
                Log.e("OrderDetail", "Không lấy được dữ liệu từ server.");
                finish();
                return;
            }

            try {
                if (!response.has("body")) {
                    Log.e("OrderDetail", "Response không có 'body': " + response.toString());
                    finish();
                    return;
                }
                JSONObject body = response.getJSONObject("body");
                JSONObject obj = body.getJSONObject("order");

                order = new Order();
                order.setId(obj.optString("_id", ""));
                String rawStatus = obj.optString("status", "").toLowerCase();
                String displayStatus;

                switch (rawStatus) {
                    case "confirmed":
                        displayStatus = "Đã xử lý";
                        break;
                    case "completed":
                        displayStatus = "Đã giao";
                        break;
                    case "notconfirmed":
                        displayStatus = "Chưa xác nhận";
                        break;
                    case "cancelled":
                        displayStatus = "Đã hủy";
                        break;
                    default:
                        displayStatus = "Không rõ";
                        break;
                }

                order.setStatus(displayStatus);
                order.setDeliveryAddress(obj.optString("address", ""));
                order.setPaymentMethod(obj.optString("paymentMethod", ""));
                order.setTotalPrice(obj.optInt("totalPrice", 0));
                order.setOrderDate(new Date(obj.optLong("createdAt", System.currentTimeMillis())));

                // Parse danh sách sản phẩm
                List<CartItem> items = new ArrayList<>();
                JSONArray itemsArray = obj.optJSONArray("items");
                if (itemsArray != null) {
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject itemObj = itemsArray.getJSONObject(i);
                        JSONObject foodObj = itemObj.getJSONObject("food");

                        CartItem item = new CartItem();
                        item.setQuantity(itemObj.optInt("quantity", 1));
                        item.setName(foodObj.optString("foodName", ""));
                        item.setPrice(foodObj.optInt("foodPrice", 0));
                        item.setImageUrl(foodObj.optString("foodImage", ""));
                        items.add(item);
                    }
                }
                order.setItems(items);

                // Cập nhật UI
                updateUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}