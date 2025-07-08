package com.example.giochaapp.models;

import java.util.Date;
import java.util.List;

public class Order {
    private String id;
    private String userId;
    private Date orderDate;
    private String status;
    private List<CartItem> items;
    private int totalPrice;
    private String deliveryAddress;
    private String customerPhone;
    private String paymentMethod;
    private String estimatedDeliveryTime;
    private String phone;

    public Order(String id, String userId, Date orderDate, String status,
                 List<CartItem> items, int totalPrice, String deliveryAddress,
                 String customerPhone, String paymentMethod) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.items = items;
        this.totalPrice = totalPrice;
        this.deliveryAddress = deliveryAddress;
        this.customerPhone = customerPhone;
        this.paymentMethod = paymentMethod;
    }

    public Order(){};

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
    public void setEstimatedDeliveryTime(String estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }

    public int getItemCount() {
        if (items == null) return 0; // ✅ tránh lỗi null
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    public String getFormattedTotalPrice() {
        return String.format("%,d ₫", totalPrice);
    }

    public String getFormattedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(orderDate);
    }

    // BỔ SUNG: Phương thức getFormattedTotal()
    public String getFormattedTotal() {
        return String.format("%,d ₫", totalPrice);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}