package com.mergeeats.deliveryservice.dto;

import com.mergeeats.common.models.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class CreateDeliveryRequest {

    @Schema(example = "order_123456789")
    @NotBlank(message = "Order ID is required")
    private String orderId;

    @Schema(example = "user_987654321")
    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @Schema(example = "restaurant_123456789")
    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    @Schema(example = "123 Main St, City, State 12345")
    @NotNull(message = "Pickup address is required")
    @Valid
    private Address pickupAddress;

    @Schema(example = "456 Oak Ave, City, State 12345")
    @NotNull(message = "Delivery address is required")
    @Valid
    private Address deliveryAddress;

    @Schema(example = "40.7128")
    @NotNull(message = "Pickup latitude is required")
    private Double pickupLatitude;

    @Schema(example = "-74.0060")
    @NotNull(message = "Pickup longitude is required")
    private Double pickupLongitude;

    @Schema(example = "40.7589")
    @NotNull(message = "Delivery latitude is required")
    private Double deliveryLatitude;

    @Schema(example = "-73.9851")
    @NotNull(message = "Delivery longitude is required")
    private Double deliveryLongitude;

    @NotNull(message = "Order total is required")
    @DecimalMin(value = "0.0", message = "Order total cannot be negative")
    private Double orderTotal;

    @DecimalMin(value = "0.0", message = "Delivery fee cannot be negative")
    private Double deliveryFee = 0.0;

    @NotNull(message = "Scheduled pickup time is required")
    private LocalDateTime scheduledPickupTime;

    @NotNull(message = "Estimated delivery time is required")
    private LocalDateTime estimatedDeliveryTime;

    @Size(max = 500, message = "Delivery instructions cannot exceed 500 characters")
    private String deliveryInstructions;

    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid customer phone number format")
    private String customerPhone;

    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid restaurant phone number format")
    private String restaurantPhone;

    private Boolean requiresSignature = false;

    private String paymentMethod;

    // Constructors
    public CreateDeliveryRequest() {}

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

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public LocalDateTime getScheduledPickupTime() {
        return scheduledPickupTime;
    }

    public void setScheduledPickupTime(LocalDateTime scheduledPickupTime) {
        this.scheduledPickupTime = scheduledPickupTime;
    }

    public LocalDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }

    public Boolean getRequiresSignature() {
        return requiresSignature;
    }

    public void setRequiresSignature(Boolean requiresSignature) {
        this.requiresSignature = requiresSignature;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}