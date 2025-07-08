package com.example.giochaapp.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giochaapp.MainActivity;
import com.example.giochaapp.R;
import com.example.giochaapp.activities.ProductDetailActivity;
import com.example.giochaapp.adapters.ProductAdapter;
import com.example.giochaapp.config.ApiConfig;
import com.example.giochaapp.models.Product;
import com.example.giochaapp.utils.AuthManager;
import com.example.giochaapp.utils.HttpHelper;
import com.example.giochaapp.utils.SharedPrefsManager;
import com.example.giochaapp.utils.ViewUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomeFragment extends Fragment {

    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupRecyclerView();
        productsRecyclerView.setHasFixedSize(false);
        productsRecyclerView.setNestedScrollingEnabled(false);
        loadProducts();

        return view;
    }

    private void initViews(View view) {
        productsRecyclerView = view.findViewById(R.id.products_recycler_view);
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), productList);

        // Gắn sự kiện click sản phẩm
        productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onAddToCartClick(Product product) {
                // Có thể thêm xử lý nếu muốn thêm nhanh
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        productsRecyclerView.setLayoutManager(gridLayoutManager);
        productsRecyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        new GetFoodsTask().execute();
    }

    private class GetFoodsTask extends AsyncTask<Void, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Void... voids) {
            try {
                SharedPrefsManager prefs = new SharedPrefsManager(getContext());
                String token = prefs.getToken();

                JSONObject response = HttpHelper.getJson(ApiConfig.BASE_URL + "/api/foods", token);
                int status = response.getInt("status");

                if (status >= 200 && status < 300) {
                    return response.getJSONArray("body");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray foods) {
            if (foods != null) {
                Log.d("FOODS_DEBUG", String.valueOf(foods.length()));
                productList.clear();
                for (int i = 0; i < foods.length(); i++) {
                    try {
                        JSONObject foodJson = foods.getJSONObject(i);
                        Product p = new Product();
                        p.setId(foodJson.optString("_id"));
                        p.setName(foodJson.optString("foodName"));
                        p.setDescription(foodJson.optString("foodDescription"));
                        p.setPrice(foodJson.optInt("foodPrice"));
                        p.setImageUrl(foodJson.optString("foodImage"));
                        p.setRating((float) foodJson.optDouble("rating", 4.5)); // fallback nếu không có
                        p.setDiscount(foodJson.optInt("discount", 0));
                        p.setCategoryId(foodJson.optString("categoryId"));

                        // Parse ingredients nếu có
                        JSONArray ingredientsArray = foodJson.optJSONArray("ingredients");
                        if (ingredientsArray != null) {
                            List<String> ingredients = new ArrayList<>();
                            for (int j = 0; j < ingredientsArray.length(); j++) {
                                ingredients.add(ingredientsArray.getString(j));
                            }
                            p.setIngredients(ingredients);
                        }

                        productList.add(p);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                productAdapter.notifyDataSetChanged();

                ViewUtils.adjustRecyclerViewHeight(productsRecyclerView, getContext(), productList.size(), 278, 2);

            } else {
                Toast.makeText(getContext(), "Không thể tải danh sách món ăn", Toast.LENGTH_SHORT).show();
            }
        }

    }

}