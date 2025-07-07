package com.example.giochaapp.models;

public class CartItem {
    private String id;
    private String productId;
    private String name;
    private String description;
    private int price;
    private String imageUrl;
    private int quantity;
    private int discount;

    public CartItem(String id, String productId, String name, String description,
                    int price, String imageUrl, int quantity) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.discount = 0;
    }

    public CartItem(String id, String productId, String name, String description,
                    int price, String imageUrl, int quantity, int discount) {
        this(id, productId, name, description, price, imageUrl, quantity);
        this.discount = discount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getDiscount() { return discount; }
    public void setDiscount(int discount) { this.discount = discount; }

    public boolean hasDiscount() { return discount > 0; }

    public int getDiscountedPrice() {
        if (hasDiscount()) {
            return price - (price * discount / 100);
        }
        return price;
    }

    public int getTotalPrice() {
        return getDiscountedPrice() * quantity;
    }

    public String getFormattedPrice() {
        return String.format("%,d ₫", price);
    }

    public String getFormattedDiscountedPrice() {
        return String.format("%,d ₫", getDiscountedPrice());
    }

    public String getFormattedTotalPrice() {
        return String.format("%,d ₫", getTotalPrice());
    }
}