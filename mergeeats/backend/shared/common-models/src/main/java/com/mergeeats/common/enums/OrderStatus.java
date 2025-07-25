package com.mergeeats.common.enums;

public enum OrderStatus {
    PENDING("Order placed, waiting for restaurant confirmation"),
    CONFIRMED("Order confirmed by restaurant"),
    PREPARING("Order is being prepared"),
    READY_FOR_PICKUP("Order is ready for pickup"),
    PICKED_UP("Order picked up by delivery partner"),
    OUT_FOR_DELIVERY("Order is out for delivery"),
    DELIVERED("Order delivered successfully"),
    CANCELLED("Order cancelled"),
    REJECTED("Order rejected by restaurant"),
    REFUNDED("Order refunded");
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isCompleted() {
        return this == DELIVERED || this == CANCELLED || this == REJECTED || this == REFUNDED;
    }
    
    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED;
    }
}