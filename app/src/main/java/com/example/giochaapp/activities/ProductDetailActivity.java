// ✅ File đã rút gọn và fix lỗi crash:

package com.example.giochaapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.giochaapp.R;
import com.example.giochaapp.models.Product;
import com.example.giochaapp.utils.DatabaseHelper;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageButton backButton, decreaseQuantity, increaseQuantity;
    private ImageView productImage;
    private TextView productName, productPrice, productRating, productDescription, quantityText, totalPrice;
    private Button addToCartButton;

    private Product currentProduct;
    private int quantity = 1;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        databaseHelper = new DatabaseHelper(this);

        initViews();
        loadProductData();
        setupClickListeners();

    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productRating = findViewById(R.id.product_rating);
        productDescription = findViewById(R.id.product_description);
        decreaseQuantity = findViewById(R.id.decrease_quantity);
        increaseQuantity = findViewById(R.id.increase_quantity);
        quantityText = findViewById(R.id.quantity_text);
        totalPrice = findViewById(R.id.total_price);
        addToCartButton = findViewById(R.id.add_to_cart_button);
    }

    private void loadProductData() {
        String productId = getIntent().getStringExtra("product_id");
        if (productId == null) {
            finish();
            return;
        }

        currentProduct = databaseHelper.getProductById(productId);
        if (currentProduct != null) {
            displayProductData();
        } else {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayProductData() {
        productName.setText(currentProduct.getName());
        productRating.setText(String.valueOf(currentProduct.getRating()));
        productDescription.setText(currentProduct.getDescription());

        Glide.with(this)
                .load(currentProduct.getImageUrl())
                .placeholder(R.drawable.product_placeholder)
                .into(productImage);

        updatePriceDisplay();
    }

    private void updatePriceDisplay() {
        if (currentProduct.hasDiscount()) {
            productPrice.setText(currentProduct.getFormattedDiscountedPrice());
        } else {
            productPrice.setText(currentProduct.getFormattedPrice());
        }

        int total = (currentProduct.hasDiscount() ?
                currentProduct.getDiscountedPrice() :
                currentProduct.getPrice()) * quantity;
        totalPrice.setText(String.format("%,d ₫", total));
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        decreaseQuantity.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityText.setText(String.valueOf(quantity));
                updatePriceDisplay();
            }
        });

        increaseQuantity.setOnClickListener(v -> {
            quantity++;
            quantityText.setText(String.valueOf(quantity));
            updatePriceDisplay();
        });

        addToCartButton.setOnClickListener(v -> addToCart());
    }

    private void addToCart() {
        boolean success = databaseHelper.addToCart(currentProduct.getId(), quantity);

        if (success) {
            Toast.makeText(this,
                    String.format("%dx %s đã được thêm vào giỏ hàng!", quantity, currentProduct.getName()),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }
}