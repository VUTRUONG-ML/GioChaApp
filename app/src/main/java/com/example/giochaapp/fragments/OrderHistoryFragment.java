package com.example.giochaapp.fragments;

import android.content.Intent;
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
import com.example.giochaapp.models.Order;
import com.example.giochaapp.utils.DatabaseHelper;

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
        databaseHelper = new DatabaseHelper(getContext());
        orderList = databaseHelper.getAllOrders();

        if (orderList == null || orderList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new OrderHistoryAdapter(getContext(), orderList, order -> {
                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                intent.putExtra("order_id", order.getId());
                startActivity(intent);
            });

            recyclerView.setAdapter(adapter);
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


}