package com.mergeeats.deliveryservice.dto;

import com.mergeeats.common.models.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public class CreateDeliveryRequest {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    @NotNull(message = "Pickup address is required")
    @Valid
    private Address pickupAddress;

    @NotNull(message = "Delivery address is required")
    @Valid
    private Address deliveryAddress;

    @DecimalMin(value = "0.0", message = "Delivery fee cannot be negative")
    private Double deliveryFee;

    @Min(value = 0, message = "Estimated delivery time cannot be negative")
    private Integer estimatedDeliveryTimeMinutes;

    private List<String> mergedOrderIds; // For merged deliveries

    private String specialInstructions;

    // Constructors
    public CreateDeliveryRequest() {}

    public CreateDeliveryRequest(String orderId, String customerId, String restaurantId,
                                Address pickupAddress, Address deliveryAddress) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Address getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(Address pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Integer getEstimatedDeliveryTimeMinutes() {
        return estimatedDeliveryTimeMinutes;
    }

    public void setEstimatedDeliveryTimeMinutes(Integer estimatedDeliveryTimeMinutes) {
        this.estimatedDeliveryTimeMinutes = estimatedDeliveryTimeMinutes;
    }

    public List<String> getMergedOrderIds() {
        return mergedOrderIds;
    }

    public void setMergedOrderIds(List<String> mergedOrderIds) {
        this.mergedOrderIds = mergedOrderIds;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
}