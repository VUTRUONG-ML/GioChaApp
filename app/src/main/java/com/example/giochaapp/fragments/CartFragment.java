package com.example.giochaapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.giochaapp.R;
import com.example.giochaapp.adapters.CartAdapter;
import com.example.giochaapp.models.CartItem;
import com.example.giochaapp.utils.DatabaseHelper;
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
                databaseHelper.updateCartItemQuantity(item.getId(), newQuantity);
                updateCartSummary();
            }

            @Override
            public void onItemRemoved(CartItem item) {
                showRemoveItemDialog(item);
            }
        });
    }

    private void loadCartItems() {
        cartItems = databaseHelper.getCartItems();
        cartAdapter.setCartItems(cartItems);

        updateUI();
        updateCartSummary();
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
                    databaseHelper.removeFromCart(item.getId());
                    loadCartItems(); // Reload cart
                    Toast.makeText(getContext(), "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
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

        // Calculate totals
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }

        subtotal.setText(String.format("%,d ₫", total));
        totalAmountDialog.setText(String.format("%,d ₫", total));

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        confirmOrderButton.setOnClickListener(v -> {
            // Process order
            processOrder();
            dialog.dismiss();
        });
    }

    private void processOrder() {
        // Create order in database
        boolean success = databaseHelper.createOrder(cartItems);

        if (success) {
            // Clear cart
            databaseHelper.clearCart();
            loadCartItems(); // Reload empty cart

            Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Lỗi khi đặt hàng", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems(); // Refresh cart when fragment becomes visible
    }
}