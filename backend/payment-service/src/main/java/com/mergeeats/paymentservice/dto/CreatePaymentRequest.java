package com.mergeeats.paymentservice.dto;

import com.mergeeats.common.models.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class CreatePaymentRequest {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency = "USD";

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "^(CREDIT_CARD|DEBIT_CARD|DIGITAL_WALLET|CASH|UPI)$", 
             message = "Payment method must be CREDIT_CARD, DEBIT_CARD, DIGITAL_WALLET, CASH, or UPI")
    private String paymentMethod;

    private String gatewayProvider; // STRIPE, RAZORPAY, PAYPAL

    private String gatewayPaymentMethodId; // For saved payment methods

    @Email(message = "Invalid email format")
    private String customerEmail;

    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String customerPhone;

    @Valid
    private Address billingAddress;

    // Group payment details
    private Boolean isGroupPayment = false;
    private String groupPaymentId;
    private Double userShareAmount;
    private Integer totalParticipants;

    // Constructors
    public CreatePaymentRequest() {}

    public CreatePaymentRequest(String orderId, String userId, Double amount, String paymentMethod) {
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getGatewayProvider() {
        return gatewayProvider;
    }

    public void setGatewayProvider(String gatewayProvider) {
        this.gatewayProvider = gatewayProvider;
    }

    public String getGatewayPaymentMethodId() {
        return gatewayPaymentMethodId;
    }

    public void setGatewayPaymentMethodId(String gatewayPaymentMethodId) {
        this.gatewayPaymentMethodId = gatewayPaymentMethodId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Boolean getIsGroupPayment() {
        return isGroupPayment;
    }

    public void setIsGroupPayment(Boolean isGroupPayment) {
        this.isGroupPayment = isGroupPayment;
    }

    public String getGroupPaymentId() {
        return groupPaymentId;
    }

    public void setGroupPaymentId(String groupPaymentId) {
        this.groupPaymentId = groupPaymentId;
    }

    public Double getUserShareAmount() {
        return userShareAmount;
    }

    public void setUserShareAmount(Double userShareAmount) {
        this.userShareAmount = userShareAmount;
    }

    public Integer getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(Integer totalParticipants) {
        this.totalParticipants = totalParticipants;
    }
}