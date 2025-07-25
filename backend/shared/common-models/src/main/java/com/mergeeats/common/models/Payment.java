package com.mergeeats.common.models;

import com.mergeeats.common.enums.PaymentStatus;
import com.mergeeats.common.enums.PaymentMethod;
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

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Payment status is required")
    @Indexed
    private PaymentStatus status = PaymentStatus.PENDING;

    // Gateway-specific information
    private String gatewayTransactionId;

    private String gatewayName; // stripe, razorpay, etc.

    private String gatewayResponse;

    // Breakdown of amounts
    @DecimalMin(value = "0.0", message = "Subtotal cannot be negative")
    private Double subtotal = 0.0;

    @DecimalMin(value = "0.0", message = "Tax amount cannot be negative")
    private Double taxAmount = 0.0;

    @DecimalMin(value = "0.0", message = "Delivery fee cannot be negative")
    private Double deliveryFee = 0.0;

    @DecimalMin(value = "0.0", message = "Platform fee cannot be negative")
    private Double platformFee = 0.0;

    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    private Double discountAmount = 0.0;

    @DecimalMin(value = "0.0", message = "Tip amount cannot be negative")
    private Double tipAmount = 0.0;

    // Currency information
    @NotBlank(message = "Currency is required")
    private String currency = "USD";

    // Customer information
    private String customerEmail;

    private String customerPhone;

    // Card/Payment details (masked for security)
    private String cardLast4;

    private String cardBrand;

    private String cardType; // credit, debit

    // Refund information
    private Boolean isRefunded = false;

    private Double refundedAmount = 0.0;

    private String refundReason;

    private LocalDateTime refundedAt;

    private String refundTransactionId;

    // Split payment for group orders
    private Boolean isSplitPayment = false;

    private String groupOrderId;

    private Double userShareAmount;

    // Receipt and invoice
    private String receiptUrl;

    private String invoiceNumber;

    // Failure information
    private String failureReason;

    private String failureCode;

    private Integer retryCount = 0;

    // Additional metadata
    private Map<String, Object> metadata;

    // Timestamps
    private LocalDateTime processedAt;

    private LocalDateTime authorizedAt;

    private LocalDateTime capturedAt;

    private LocalDateTime failedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Constructors
    public Payment() {}

    public Payment(String orderId, String userId, Double amount, PaymentMethod paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    public void setGatewayTransactionId(String gatewayTransactionId) {
        this.gatewayTransactionId = gatewayTransactionId;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getGatewayResponse() {
        return gatewayResponse;
    }

    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Double getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(Double platformFee) {
        this.platformFee = platformFee;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(Double tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Boolean getIsRefunded() {
        return isRefunded;
    }

    public void setIsRefunded(Boolean isRefunded) {
        this.isRefunded = isRefunded;
    }

    public Double getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(Double refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    public String getRefundTransactionId() {
        return refundTransactionId;
    }

    public void setRefundTransactionId(String refundTransactionId) {
        this.refundTransactionId = refundTransactionId;
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

    public Double getUserShareAmount() {
        return userShareAmount;
    }

    public void setUserShareAmount(Double userShareAmount) {
        this.userShareAmount = userShareAmount;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public LocalDateTime getAuthorizedAt() {
        return authorizedAt;
    }

    public void setAuthorizedAt(LocalDateTime authorizedAt) {
        this.authorizedAt = authorizedAt;
    }

    public LocalDateTime getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(LocalDateTime capturedAt) {
        this.capturedAt = capturedAt;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
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

    // Helper methods
    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }

    public boolean isPending() {
        return status == PaymentStatus.PENDING || status == PaymentStatus.PROCESSING;
    }

    public void incrementRetryCount() {
        this.retryCount = (this.retryCount == null) ? 1 : this.retryCount + 1;
    }

    public Double getTotalAmount() {
        return (subtotal != null ? subtotal : 0.0) +
               (taxAmount != null ? taxAmount : 0.0) +
               (deliveryFee != null ? deliveryFee : 0.0) +
               (platformFee != null ? platformFee : 0.0) +
               (tipAmount != null ? tipAmount : 0.0) -
               (discountAmount != null ? discountAmount : 0.0);
    }

    public String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
}