package com.example.giochaapp.models;

import java.util.List;

public class Product {
    private String id;
    private String name;
    private String description;
    private int price;
    private String imageUrl;
    private float rating;
    private String categoryId;

    private int discount; // percentage
    private List<String> ingredients;


    public Product(String id, String name, String description, int price,
                   String imageUrl, float rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.discount = 0;
    }

    public Product() {
        // Constructor mặc định để có thể tạo rồi gán lần lượt
    }


    public Product(String id, String name, String description, int price,
                   String imageUrl, float rating, int discount) {
        this(id, name, description, price, imageUrl, rating);
        this.discount = discount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getDiscount() { return discount; }
    public void setDiscount(int discount) { this.discount = discount; }

    public List<String> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public boolean hasDiscount() { return discount > 0; }

    public int getDiscountedPrice() {
        if (hasDiscount()) {
            return price - (price * discount / 100);
        }
        return price;
    }
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    public String getFormattedIngredients() {
        if (ingredients == null || ingredients.isEmpty()) return "Không rõ";
        return android.text.TextUtils.join(", ", ingredients);
    }



    public String getFormattedPrice() {
        return String.format("%,d ₫", price);
    }

    public String getFormattedDiscountedPrice() {
        return String.format("%,d ₫", getDiscountedPrice());
    }
}