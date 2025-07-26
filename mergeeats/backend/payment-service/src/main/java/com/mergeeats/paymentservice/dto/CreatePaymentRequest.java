package com.mergeeats.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class CreatePaymentRequest {

    @Schema(example = "order_123456789")
    @NotBlank(message = "Order ID is required")
    private String orderId;

    @Schema(example = "user_987654321")
    @NotBlank(message = "User ID is required")
    private String userId;

    @Schema(example = "29.99")
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;

    @Schema(example = "CREDIT_CARD")
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @Schema(example = "ONLINE")
    @NotBlank(message = "Payment type is required")
    private String paymentType;

    // Constructors
    public CreatePaymentRequest() {}

    public CreatePaymentRequest(String orderId, String userId, Double amount, String paymentMethod, String paymentType) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentType = paymentType;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
