package com.example.giochaapp.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giochaapp.R;
import com.example.giochaapp.activities.OrderDetailActivity;
import com.example.giochaapp.adapters.OrderHistoryAdapter;
import com.example.giochaapp.config.ApiConfig;
import com.example.giochaapp.models.CartItem;
import com.example.giochaapp.models.Order;
import com.example.giochaapp.utils.DatabaseHelper;
import com.example.giochaapp.utils.HttpHelper;
import com.example.giochaapp.utils.SharedPrefsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout emptyStateLayout;
    private OrderHistoryAdapter adapter;
    private DatabaseHelper databaseHelper;
    private List<Order> orderList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_orders);
        emptyStateLayout = view.findViewById(R.id.empty_state_layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>(); // Khởi tạo danh sách trống

        new GetUserOrdersTask().execute();


        if (orderList == null || orderList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        }

        // Xử lý nút quay lại
        View backButton = view.findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }

        return view;
    }

    private void updateUI() {
        if (orderList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }



    private class GetUserOrdersTask extends AsyncTask<Void, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Void... voids) {
            try {
                SharedPrefsManager prefs = new SharedPrefsManager(getContext());
                String token = prefs.getToken();
                JSONObject result = HttpHelper.getJson(ApiConfig.BASE_URL + "/api/orders/userOrders", token);

                if (result != null && result.has("status")) {
                    int status = result.getInt("status");
                    if (status >= 200 && status < 300) {
                        // Dữ liệu trả về là một mảng JSON
                        return result.getJSONArray("body");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray ordersJson) {
            if (ordersJson != null) {
                orderList.clear();
                for (int i = 0; i < ordersJson.length(); i++) {
                    try {
                        JSONObject obj = ordersJson.getJSONObject(i);
                        Order order = new Order();
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
                        JSONArray itemsArray = obj.optJSONArray("items");
                        if (itemsArray != null) {
                            List<CartItem> items = new ArrayList<>();
                            for (int j = 0; j < itemsArray.length(); j++) {
                                JSONObject itemObj = itemsArray.getJSONObject(j);
                                CartItem item = new CartItem();
                                item.setQuantity(itemObj.optInt("quantity", 1));
                                // Optional: nếu có food thì lấy name, image, price
                                items.add(item);
                            }
                            order.setItems(items);
                        }
                        orderList.add(order);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                adapter = new OrderHistoryAdapter(getContext(), orderList, order -> {
                    Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                    intent.putExtra("order_id", order.getId());
                    startActivity(intent);
                });

                recyclerView.setAdapter(adapter);

                updateUI();
            } else {
                // Show empty or error
                emptyStateLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }


}