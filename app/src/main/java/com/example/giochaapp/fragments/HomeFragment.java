package com.example.giochaapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.giochaapp.R;
import com.example.giochaapp.activities.ProductDetailActivity;
import com.example.giochaapp.adapters.ProductAdapter;
import com.example.giochaapp.models.Product;
import java.util.ArrayList;
import java.util.List;

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
        // Sample data - thay thế bằng DB sau
        productList.add(new Product("1", "Giò Chả Truyền Thống",
                "Giò chả làm theo công thức truyền thống", 25000,
                "https://images.pexels.com/photos/4518843/pexels-photo-4518843.jpeg", 4.8f));
        productList.add(new Product("2", "Combo Giò Chả",
                "Giò chả kèm rau sống và bánh tráng", 35000,
                "https://images.pexels.com/photos/4253312/pexels-photo-4253312.jpeg", 4.9f, 10));
        productList.add(new Product("3", "Giò Chả Cao Cấp",
                "Giò chả chất lượng cao với thảo mộc đặc biệt", 45000,
                "https://images.pexels.com/photos/5419336/pexels-photo-5419336.jpeg", 4.7f));
        productList.add(new Product("4", "Gói Gia Đình",
                "5 miếng giò chả các loại khác nhau", 120000,
                "https://images.pexels.com/photos/6210959/pexels-photo-6210959.jpeg", 4.6f, 15));

        productAdapter.notifyDataSetChanged();
    }
}