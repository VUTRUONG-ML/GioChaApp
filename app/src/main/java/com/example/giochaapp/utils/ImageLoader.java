package com.example.giochaapp.utils;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.giochaapp.R;

public class ImageLoader {

    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.product_placeholder)
                .error(R.drawable.product_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();

        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .into(imageView);
    }

    public static void loadCircularImage(Context context, String imageUrl, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.logo_placeholder)
                .error(R.drawable.logo_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop();

        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .into(imageView);
    }

    public static void loadBannerImage(Context context, String imageUrl, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.banner_placeholder)
                .error(R.drawable.banner_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();

        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .into(imageView);
    }
}