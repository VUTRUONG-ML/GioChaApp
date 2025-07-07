package com.example.giochaapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.giochaapp.R;
import com.example.giochaapp.models.Product;
import com.example.giochaapp.utils.DatabaseHelper;
import com.example.giochaapp.utils.ImageLoader;

public class ProductDetailFragment extends Fragment {

    private static final String ARG_PRODUCT_ID = "product_id";

    private ImageView productImage;
    private TextView productName;
    private TextView productDescription;
    private TextView productPrice;
    private TextView quantityText;
    private ImageButton btnMinus;
    private ImageButton btnPlus;
    private Button btnAddToCart;

    private Product product;
    private int quantity = 1;
    private DatabaseHelper databaseHelper;

    public static ProductDetailFragment newInstance(String productId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        initViews(view);
        loadProductData();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        productImage = view.findViewById(R.id.product_image);
        productName = view.findViewById(R.id.product_name);
        productDescription = view.findViewById(R.id.product_description);
        productPrice = view.findViewById(R.id.product_price);
        quantityText = view.findViewById(R.id.quantity_text);
        btnMinus = view.findViewById(R.id.quantity_minus);
        btnPlus = view.findViewById(R.id.quantity_plus);
        btnAddToCart = view.findViewById(R.id.add_to_cart_detail_button);

        databaseHelper = new DatabaseHelper(getContext());
    }

    private void loadProductData() {
        if (getArguments() != null) {
            String productId = getArguments().getString(ARG_PRODUCT_ID);
            if (productId != null) {
                product = databaseHelper.getProductById(productId);
                if (product != null) {
                    displayProductInfo();
                }
            }
        }
    }

    private void displayProductInfo() {
        productName.setText(product.getName());
        productDescription.setText(product.getDescription());
        productPrice.setText(product.getFormattedPrice());

        ImageLoader.loadImage(getContext(), product.getImageUrl(), productImage);
        updateQuantityDisplay();
    }

    private void setupClickListeners() {
        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityDisplay();
            }
        });

        btnPlus.setOnClickListener(v -> {
            quantity++;
            updateQuantityDisplay();
        });

        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void updateQuantityDisplay() {
        quantityText.setText(String.valueOf(quantity));
    }

    private void addToCart() {
        boolean success = databaseHelper.addToCart(product.getId(), quantity);
        if (success) {
            Toast.makeText(getContext(), "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
        }
    }
}