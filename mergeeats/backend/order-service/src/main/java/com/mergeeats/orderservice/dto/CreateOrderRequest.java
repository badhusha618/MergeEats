package com.mergeeats.orderservice.dto;

import com.mergeeats.common.models.Address;
import com.mergeeats.common.models.OrderItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CreateOrderRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;
    
    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItem> items;
    
    @NotNull(message = "Delivery address is required")
    private Address deliveryAddress;
    
    private String specialInstructions;
    
    private boolean isGroupOrder = false;
    
    private String groupOrderId;
    
    // Constructors
    public CreateOrderRequest() {}
    
    public CreateOrderRequest(String userId, String restaurantId, List<OrderItem> items, Address deliveryAddress) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    
    public Address getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    public boolean isGroupOrder() {
        return isGroupOrder;
    }
    
    public void setGroupOrder(boolean groupOrder) {
        isGroupOrder = groupOrder;
    }
    
    public String getGroupOrderId() {
        return groupOrderId;
    }
    
    public void setGroupOrderId(String groupOrderId) {
        this.groupOrderId = groupOrderId;
    }
}