package com.example.giochaapp.utils;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class ViewUtils {
    public static void adjustRecyclerViewHeight(RecyclerView recyclerView, Context context, int itemCount, int itemHeightDp, int spanCount) {
        if (recyclerView == null || context == null) return;

        int numRows = (int) Math.ceil(itemCount / (float) spanCount);
        float density = context.getResources().getDisplayMetrics().density;
        int totalHeightPx = (int) (numRows * itemHeightDp * density);

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = totalHeightPx;
        recyclerView.setLayoutParams(params);
    }
}
