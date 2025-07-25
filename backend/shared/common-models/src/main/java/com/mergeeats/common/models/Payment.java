package com.mergeeats.common.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "payments")
public class Payment {

    @Id
    private String paymentId;

    @NotBlank(message = "Order ID is required")
    @Indexed
    private String orderId;

    @NotBlank(message = "User ID is required")
    @Indexed
    private String userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Currency is required")
    private String currency = "USD";

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, DIGITAL_WALLET, CASH, UPI

    @NotBlank(message = "Payment status is required")
    private String paymentStatus; // PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED

    @NotBlank(message = "Payment type is required")
    private String paymentType; // ORDER_PAYMENT, REFUND, PARTIAL_REFUND

    // Payment gateway details
    private String gatewayProvider; // STRIPE, RAZORPAY, PAYPAL
    private String gatewayTransactionId;
    private String gatewayPaymentMethodId;
    private Map<String, Object> gatewayResponse;

    // Transaction details
    private String transactionReference;
    private String authorizationCode;
    private String receiptUrl;

    // Fee breakdown
    @DecimalMin(value = "0.0", message = "Platform fee cannot be negative")
    private Double platformFee = 0.0;

    @DecimalMin(value = "0.0", message = "Gateway fee cannot be negative")
    private Double gatewayFee = 0.0;

    @DecimalMin(value = "0.0", message = "Tax amount cannot be negative")
    private Double taxAmount = 0.0;

    @DecimalMin(value = "0.0", message = "Net amount cannot be negative")
    private Double netAmount;

    // Refund details
    private String refundReason;
    private String refundReference;
    private Double refundAmount;
    private LocalDateTime refundInitiatedAt;
    private LocalDateTime refundCompletedAt;

    // Group payment details (for split payments)
    private String groupPaymentId;
    private Boolean isGroupPayment = false;
    private Double userShareAmount;
    private Integer totalParticipants;

    // Timestamps
    private LocalDateTime initiatedAt;
    private LocalDateTime authorizedAt;
    private LocalDateTime completedAt;
    private LocalDateTime failedAt;
    private LocalDateTime cancelledAt;

    // Retry information
    private Integer retryAttempts = 0;
    private Integer maxRetryAttempts = 3;
    private LocalDateTime nextRetryAt;

    // Failure details
    private String failureReason;
    private String failureCode;

    // Customer details
    private String customerEmail;
    private String customerPhone;
    private Address billingAddress;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Constructors
    public Payment() {}

    public Payment(String orderId, String userId, Double amount, String paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = "PENDING";
        this.paymentType = "ORDER_PAYMENT";
        this.netAmount = amount;
        this.initiatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getGatewayProvider() {
        return gatewayProvider;
    }

    public void setGatewayProvider(String gatewayProvider) {
        this.gatewayProvider = gatewayProvider;
    }

    public String getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    public void setGatewayTransactionId(String gatewayTransactionId) {
        this.gatewayTransactionId = gatewayTransactionId;
    }

    public String getGatewayPaymentMethodId() {
        return gatewayPaymentMethodId;
    }

    public void setGatewayPaymentMethodId(String gatewayPaymentMethodId) {
        this.gatewayPaymentMethodId = gatewayPaymentMethodId;
    }

    public Map<String, Object> getGatewayResponse() {
        return gatewayResponse;
    }

    public void setGatewayResponse(Map<String, Object> gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public Double getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(Double platformFee) {
        this.platformFee = platformFee;
    }

    public Double getGatewayFee() {
        return gatewayFee;
    }

    public void setGatewayFee(Double gatewayFee) {
        this.gatewayFee = gatewayFee;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Double netAmount) {
        this.netAmount = netAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getRefundReference() {
        return refundReference;
    }

    public void setRefundReference(String refundReference) {
        this.refundReference = refundReference;
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public LocalDateTime getRefundInitiatedAt() {
        return refundInitiatedAt;
    }

    public void setRefundInitiatedAt(LocalDateTime refundInitiatedAt) {
        this.refundInitiatedAt = refundInitiatedAt;
    }

    public LocalDateTime getRefundCompletedAt() {
        return refundCompletedAt;
    }

    public void setRefundCompletedAt(LocalDateTime refundCompletedAt) {
        this.refundCompletedAt = refundCompletedAt;
    }

    public String getGroupPaymentId() {
        return groupPaymentId;
    }

    public void setGroupPaymentId(String groupPaymentId) {
        this.groupPaymentId = groupPaymentId;
    }

    public Boolean getIsGroupPayment() {
        return isGroupPayment;
    }

    public void setIsGroupPayment(Boolean isGroupPayment) {
        this.isGroupPayment = isGroupPayment;
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

    public LocalDateTime getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(LocalDateTime initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    public LocalDateTime getAuthorizedAt() {
        return authorizedAt;
    }

    public void setAuthorizedAt(LocalDateTime authorizedAt) {
        this.authorizedAt = authorizedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public Integer getRetryAttempts() {
        return retryAttempts;
    }

    public void setRetryAttempts(Integer retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public Integer getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(Integer maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public LocalDateTime getNextRetryAt() {
        return nextRetryAt;
    }

    public void setNextRetryAt(LocalDateTime nextRetryAt) {
        this.nextRetryAt = nextRetryAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(String failureCode) {
        this.failureCode = failureCode;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}