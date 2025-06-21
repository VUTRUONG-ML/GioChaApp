package com.example.giochaapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.giochaapp.auth.LoginActivity;
import com.example.giochaapp.auth.RegisterActivity;
import com.example.giochaapp.orders.OrderActivity;
import com.example.giochaapp.orders.OrderDetailActivity;
import com.example.giochaapp.orders.OrderHistoryActivity;
import com.example.giochaapp.products.ProductDetailActivity;
import com.example.giochaapp.products.ProductListActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnLogin).setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        findViewById(R.id.btnRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        findViewById(R.id.btnProductList).setOnClickListener(v ->
                startActivity(new Intent(this, ProductListActivity.class)));

        findViewById(R.id.btnProductDetail).setOnClickListener(v ->
                startActivity(new Intent(this, ProductDetailActivity.class)));

        findViewById(R.id.btnOrder).setOnClickListener(v ->
                startActivity(new Intent(this, OrderActivity.class)));

        findViewById(R.id.btnOrderHistory).setOnClickListener(v ->
                startActivity(new Intent(this, OrderHistoryActivity.class)));

        findViewById(R.id.btnOrderDetail).setOnClickListener(v ->
                startActivity(new Intent(this, OrderDetailActivity.class)));

    }
}