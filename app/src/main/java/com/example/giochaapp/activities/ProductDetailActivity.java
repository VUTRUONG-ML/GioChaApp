// ✅ File đã rút gọn và fix lỗi crash:

package com.example.giochaapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.giochaapp.R;
import com.example.giochaapp.config.ApiConfig;
import com.example.giochaapp.models.Product;
import com.example.giochaapp.utils.DatabaseHelper;
import com.example.giochaapp.utils.HttpHelper;
import com.example.giochaapp.utils.SharedPrefsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

        new FetchProductDetailTask().execute(productId);
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
        if (currentProduct != null) {
            new AddToCartTask(currentProduct.getId(), quantity).execute();
        }
    }

    private class FetchProductDetailTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String productId = params[0];
            String url = ApiConfig.BASE_URL + "/api/foods/" + productId;

            try {
                SharedPrefsManager prefs = new SharedPrefsManager(ProductDetailActivity.this);
                String token = prefs.getToken();
                return HttpHelper.getJson(url, token); // ⬅ sử dụng lại hàm tiện ích
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
                    Object body = result.get("body");

                    if (statusCode >= 200 && statusCode < 300 && body instanceof JSONObject) {
                        JSONObject json = (JSONObject) body;
                        Log.d("PRODUCT_DETAIL", "JSON Response: " + json.toString());

                        JSONObject foodObj = json.optJSONObject("food");
                        if (foodObj != null) {
                            Product product = parseProduct(foodObj);
                            currentProduct = product;
                            Log.d("PRODUCT_DETAIL", "Tên sản phẩm: " + product.getName());
                            displayProductData();
                        } else {
                            Toast.makeText(ProductDetailActivity.this, "Không tìm thấy dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        JSONObject errorBody = (body instanceof JSONObject) ? (JSONObject) body : null;
                        String message = (errorBody != null) ? errorBody.optString("message", "Lỗi server") : "Lỗi không xác định";
                        Toast.makeText(ProductDetailActivity.this, "Lỗi " + statusCode + ": " + message, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ProductDetailActivity.this, "Lỗi xử lý phản hồi từ server!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(ProductDetailActivity.this, "Không thể kết nối đến server!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        private Product parseProduct(JSONObject json) {
            Product product = new Product();
            product.setId(json.optString("_id"));
            product.setName(json.optString("foodName"));
            product.setDescription(json.optString("foodDescription"));
            product.setPrice(json.optInt("foodPrice"));
            product.setImageUrl(json.optString("foodImage"));
            product.setRating((float) json.optDouble("rating", 4.5));
            product.setDiscount(json.optInt("discount", 0));
            product.setCategoryId(json.optString("foodCategoryId"));

            JSONArray ingredientsArray = json.optJSONArray("ingredients");
            if (ingredientsArray != null) {
                List<String> ingredients = new ArrayList<>();
                for (int i = 0; i < ingredientsArray.length(); i++) {
                    ingredients.add(ingredientsArray.optString(i));
                }
                product.setIngredients(ingredients);
            }

            return product;
        }
    }


    // thêm vào giỏ hàng
    private class AddToCartTask extends AsyncTask<Void, Void, JSONObject> {
        private final String foodId;
        private final int quantity;

        public AddToCartTask(String foodId, int quantity) {
            this.foodId = foodId;
            this.quantity = quantity;
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {

            try {
                // Token
                SharedPrefsManager prefs = new SharedPrefsManager(ProductDetailActivity.this);
                String token = prefs.getToken();

                JSONObject body = new JSONObject();
                body.put("foodId", foodId);
                body.put("quantity", quantity);

                return HttpHelper.postJson(ApiConfig.BASE_URL + "/api/cart/addToCart", body, token);
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
                        Toast.makeText(ProductDetailActivity.this,
                                String.format("%dx %s đã được thêm vào giỏ hàng!", quantity, currentProduct.getName()),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetailActivity.this,
                                "Lỗi " + statusCode + ": " + message,
                                Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ProductDetailActivity.this, "Lỗi khi xử lý phản hồi từ server!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProductDetailActivity.this, "Không thể kết nối đến server!", Toast.LENGTH_SHORT).show();
            }
        }
    }


}