package com.example.giochaapp.models;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String avatarUrl;
    private String memberSince;
    private int totalOrders;
    private float rating;

    public User(String id, String name, String email, String phone,
                String avatarUrl, String memberSince, int totalOrders, float rating) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.memberSince = memberSince;
        this.totalOrders = totalOrders;
        this.rating = rating;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getMemberSince() { return memberSince; }
    public void setMemberSince(String memberSince) { this.memberSince = memberSince; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
}