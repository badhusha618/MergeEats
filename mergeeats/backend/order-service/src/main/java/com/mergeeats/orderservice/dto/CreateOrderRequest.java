package com.mergeeats.orderservice.dto;

import com.mergeeats.common.models.Address;
import com.mergeeats.common.models.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateOrderRequest {
    @Schema(example = "user_987654321")
    @NotNull(message = "User ID is required")
    private String userId;

    @Schema(example = "restaurant_123456789")
    @NotNull(message = "Restaurant ID is required")
    private String restaurantId;

    @Schema(example = "123 Main St, City, State 12345")
    @NotNull(message = "Delivery address is required")
    private Address deliveryAddress;

    @Schema(example = "Please deliver to the front door")
    @Size(max = 200, message = "Special instructions cannot exceed 200 characters")
    private String specialInstructions;

    @Schema(example = "CREDIT_CARD")
    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    @Schema(example = "[{\"menuItemId\":\"item_123\",\"quantity\":2,\"specialInstructions\":\"Extra cheese please\"}]")
    @NotEmpty(message = "Order items are required")
    private List<OrderItem> items;

    @Schema(example = "false")
    private boolean isGroupOrder;

    @Schema(example = "group_order_123")
    private String groupOrderId;

    // Constructors
    public CreateOrderRequest() {}

    public CreateOrderRequest(String userId, String restaurantId, List<OrderItem> items, Address deliveryAddress) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
    }

    public CreateOrderRequest(String userId, String restaurantId, List<OrderItem> items, Address deliveryAddress, 
                            String specialInstructions, String paymentMethod, boolean isGroupOrder, String groupOrderId) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.specialInstructions = specialInstructions;
        this.paymentMethod = paymentMethod;
        this.isGroupOrder = isGroupOrder;
        this.groupOrderId = groupOrderId;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isGroupOrder() {
        return isGroupOrder;
    }

    public void setGroupOrder(boolean groupOrder) {
        this.isGroupOrder = groupOrder;
    }

    public String getGroupOrderId() {
        return groupOrderId;
    }

    public void setGroupOrderId(String groupOrderId) {
        this.groupOrderId = groupOrderId;
    }
}