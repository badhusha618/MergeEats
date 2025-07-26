package com.mergeeats.paymentservice.dto;

import com.mergeeats.common.enums.PaymentMethod;
import com.mergeeats.common.enums.PaymentType;
import jakarta.validation.constraints.*;
import java.util.Map;

public class CreatePaymentRequest {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency = "INR";

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    // Split payment details
    private Boolean isSplitPayment = false;

    private String groupOrderId;

    private Map<String, Double> splitDetails; // userId -> amount

    // Constructors
    public CreatePaymentRequest() {}

    public CreatePaymentRequest(String orderId, String userId, Double amount, 
                              PaymentMethod paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsSplitPayment() {
        return isSplitPayment;
    }

    public void setIsSplitPayment(Boolean isSplitPayment) {
        this.isSplitPayment = isSplitPayment;
    }

    public String getGroupOrderId() {
        return groupOrderId;
    }

    public void setGroupOrderId(String groupOrderId) {
        this.groupOrderId = groupOrderId;
    }

    public Map<String, Double> getSplitDetails() {
        return splitDetails;
    }

    public void setSplitDetails(Map<String, Double> splitDetails) {
        this.splitDetails = splitDetails;
    }
}