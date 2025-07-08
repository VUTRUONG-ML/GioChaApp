package com.example.giochaapp.fragments;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.giochaapp.R;
import com.example.giochaapp.adapters.CartAdapter;
import com.example.giochaapp.config.ApiConfig;
import com.example.giochaapp.models.CartItem;
import com.example.giochaapp.utils.DatabaseHelper;
import com.example.giochaapp.utils.HttpHelper;
import com.example.giochaapp.utils.SharedPrefsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private LinearLayout emptyCartLayout;
    private LinearLayout cartFooter;
    private TextView itemCount;
    private TextView totalAmount;
    private Button checkoutButton;

    private CartAdapter cartAdapter;
    private DatabaseHelper databaseHelper;
    private List<CartItem> cartItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        databaseHelper = new DatabaseHelper(getContext());

        cartItems = new ArrayList<>();

        initViews(view);
        setupRecyclerView();
        loadCartItems();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        cartRecyclerView = view.findViewById(R.id.cart_recycler_view);
        emptyCartLayout = view.findViewById(R.id.empty_cart_layout);
        cartFooter = view.findViewById(R.id.cart_footer);
        itemCount = view.findViewById(R.id.item_count);
        totalAmount = view.findViewById(R.id.total_amount);
        checkoutButton = view.findViewById(R.id.checkout_button);
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(getContext());
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartRecyclerView.setAdapter(cartAdapter);

        cartAdapter.setOnCartItemListener(new CartAdapter.OnCartItemListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQuantity) {
                item.setQuantity(newQuantity);
                new UpdateCartTask(item.getProductId(), newQuantity).execute();
                updateCartSummary();
            }

            @Override
            public void onItemRemoved(CartItem item) {
                showRemoveItemDialog(item);
            }
        });
    }

    private void loadCartItems() {
        new GetCartTask().execute();
    }

    private void updateUI() {
        if (cartItems.isEmpty()) {
            cartRecyclerView.setVisibility(View.GONE);
            cartFooter.setVisibility(View.GONE);
            emptyCartLayout.setVisibility(View.VISIBLE);
        } else {
            cartRecyclerView.setVisibility(View.VISIBLE);
            cartFooter.setVisibility(View.VISIBLE);
            emptyCartLayout.setVisibility(View.GONE);
        }
    }

    private void updateCartSummary() {
        int totalItems = 0;
        int totalPrice = 0;

        for (CartItem item : cartItems) {
            totalItems += item.getQuantity();
            totalPrice += item.getTotalPrice();
        }

        itemCount.setText(totalItems + " sản phẩm");
        totalAmount.setText(String.format("%,d ₫", totalPrice));
    }

    private void setupClickListeners() {
        checkoutButton.setOnClickListener(v -> proceedToCheckout());
    }

    private void showRemoveItemDialog(CartItem item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    new RemoveCartItemTask(item.getProductId()).execute();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void proceedToCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show checkout dialog
        showCheckoutDialog();
    }

    private void showCheckoutDialog() {
        // Create and show checkout dialog
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_checkout, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        // Setup dialog views and listeners
        setupCheckoutDialog(dialogView, dialog);

        dialog.show();
    }

    private void setupCheckoutDialog(View dialogView, AlertDialog dialog) {
        TextView subtotal = dialogView.findViewById(R.id.subtotal);
        TextView totalAmountDialog = dialogView.findViewById(R.id.total_amount);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button confirmOrderButton = dialogView.findViewById(R.id.confirm_order_button);
        TextView addressView = dialogView.findViewById(R.id.delivery_address);
        RadioGroup paymentGroup = dialogView.findViewById(R.id.payment_method_group);
        TextView changeAddress = dialogView.findViewById(R.id.change_address);

        changeAddress.setOnClickListener(v -> {
            // Tạo dialog nhập địa chỉ mới
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Nhập địa chỉ mới");

            final EditText input = new EditText(getContext());
            input.setHint("Nhập địa chỉ giao hàng");
            input.setText(addressView.getText().toString()); // gợi ý địa chỉ cũ

            builder.setView(input);

            builder.setPositiveButton("Xác nhận", (dialogInterface, which) -> {
                String newAddress = input.getText().toString().trim();
                if (!newAddress.isEmpty()) {
                    addressView.setText(newAddress);
                } else {
                    Toast.makeText(getContext(), "Địa chỉ không được để trống", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Hủy", (dialogInterface, which) -> dialogInterface.dismiss());

            builder.show();
        });

        // Calculate totals
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }

        subtotal.setText(String.format("%,d ₫", total));
        totalAmountDialog.setText(String.format("%,d ₫", total));

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        confirmOrderButton.setOnClickListener(v -> {
            String address = addressView.getText().toString().trim();
            String paymentMethod;

            int selectedId = paymentGroup.getCheckedRadioButtonId();
            if (selectedId == R.id.cash_payment) {
                paymentMethod = "cash";
            } else{
                paymentMethod = "card";
            }
            processOrder(address, paymentMethod);
            dialog.dismiss();
        });
    }

    private void processOrder(String address, String paymentMethod) {

        new CreateOrderTask(address, paymentMethod).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems(); // Refresh cart when fragment becomes visible
    }

    private class GetCartTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                SharedPrefsManager prefs = new SharedPrefsManager(getContext());
                String token = prefs.getToken();

                return HttpHelper.getJson(ApiConfig.BASE_URL + "/api/cart", token);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                Log.d("CART_API_RAW", result.toString());
                try {
                    int statusCode = result.getInt("status");
                    if (statusCode >= 200 && statusCode < 300) {
                        JSONObject body = result.getJSONObject("body");
                        JSONArray items = body.optJSONArray("items");
                        Log.d("CART_DEBUG", "Tổng số items: " + items.length());
                        cartItems.clear();
                        if (items != null) {
                            for (int i = 0; i < items.length(); i++) {
                                try {
                                    JSONObject item = items.getJSONObject(i);

                                    if (!item.has("food")) {
                                        Log.e("CART_API_ERROR", "Item không có food");
                                        continue;
                                    }

                                    JSONObject food = item.getJSONObject("food");

                                    Log.d("CART_ITEM", "foodId: " + food.optString("_id") + ", quantity: " + item.optInt("quantity"));

                                    CartItem cartItem = new CartItem(
                                            item.optString("_id", ""),                      // ID của item trong giỏ hàng
                                            food.optString("_id", ""),                      // productId
                                            food.optString("foodName", ""),
                                            food.optString("foodDescription", ""),
                                            food.optInt("foodPrice", 0),
                                            food.optString("foodImage", ""),
                                            item.optInt("quantity", 1),
                                            food.optInt("discount", 0)
                                    );

                                    cartItems.add(cartItem);
                                } catch (Exception ex) {
                                    Log.e("CART_ITEM_ERROR", "Lỗi khi xử lý item tại index " + i, ex);
                                }
                            }

                        }
                        else {
                            Log.e("CART_API_ERROR", "items is null hoặc không phải JSONArray");
                            return;
                        }


                        cartAdapter.setCartItems(cartItems);
                        updateUI();
                        updateCartSummary();

                    } else {
                        String message = result.getJSONObject("body").optString("message", "Không thể lấy giỏ hàng");
                        Toast.makeText(getContext(), "Lỗi " + statusCode + ": " + message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Lỗi khi xử lý phản hồi từ server!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateCartTask extends AsyncTask<Void, Void, JSONObject> {
        private final String foodId;
        private final int quantity;

        public UpdateCartTask(String foodId, int quantity) {
            this.foodId = foodId;
            this.quantity = quantity;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                SharedPrefsManager prefs = new SharedPrefsManager(getContext());
                String token = prefs.getToken();

                JSONObject body = new JSONObject();
                body.put("foodId", foodId);
                body.put("quantity", quantity);

                return HttpHelper.putJson(ApiConfig.BASE_URL + "/api/cart/updateCart", body, token);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                try {
                    int statusCode = result.getInt("status");
                    JSONObject responseBody = result.getJSONObject("body");
                    String message = responseBody.optString("message", "Không rõ phản hồi");

                    if (statusCode >= 200 && statusCode < 300) {
                        Log.d("CART_UPDATE", "Cập nhật giỏ hàng thành công");
                    } else {
                        Toast.makeText(getContext(), "Lỗi " + statusCode + ": " + message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Lỗi khi xử lý phản hồi từ server!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Không thể kết nối đến server!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RemoveCartItemTask extends AsyncTask<Void, Void, JSONObject> {
        private final String foodId;

        public RemoveCartItemTask(String foodId) {
            this.foodId = foodId;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                SharedPrefsManager prefs = new SharedPrefsManager(getContext());
                String token = prefs.getToken();
                return HttpHelper.deleteJson(ApiConfig.BASE_URL + "/api/cart/removeFromCart/" + foodId, token);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                try {
                    int status = result.getInt("status");
                    if (status >= 200 && status < 300) {
                        Toast.makeText(getContext(), "Đã xóa sản phẩm khỏi giỏ", Toast.LENGTH_SHORT).show();
                        loadCartItems(); // reload lại giỏ hàng
                    } else {
                        String message = result.getJSONObject("body").optString("message", "Xóa thất bại");
                        Toast.makeText(getContext(), "Lỗi " + status + ": " + message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Lỗi xử lý phản hồi!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Không thể kết nối server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CreateOrderTask extends AsyncTask<Void, Void, JSONObject> {
        private final String address;
        private final String paymentMethod;

        public CreateOrderTask(String address, String paymentMethod) {
            this.address = address;
            this.paymentMethod = paymentMethod;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                SharedPrefsManager prefs = new SharedPrefsManager(getContext());
                String token = prefs.getToken();

                JSONObject body = new JSONObject();
                body.put("address", address);
                body.put("paymentMethod", paymentMethod);

                return HttpHelper.postJson(ApiConfig.BASE_URL + "/api/orders/create", body, token);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                try {
                    int status = result.getInt("status");
                    JSONObject responseBody = result.getJSONObject("body");

                    if (status >= 200 && status < 300) {
                        Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                        loadCartItems(); // Reload lại cart rỗng sau khi đã đặt
                    } else {
                        String message = responseBody.optString("message", "Lỗi không rõ");
                        Toast.makeText(getContext(), "Lỗi " + status + ": " + message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Lỗi xử lý phản hồi server!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        }
    }


}