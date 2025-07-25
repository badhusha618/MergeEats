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
import java.util.List;
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

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;

    @NotNull(message = "Status is required")
    private PaymentStatus status = PaymentStatus.PENDING;

    private String transactionId; // External payment gateway transaction ID

    private String gatewayPaymentId; // Stripe/Razorpay payment ID

    private String gatewayOrderId; // Gateway order ID

    @DecimalMin(value = "0.0", message = "Platform fee cannot be negative")
    private Double platformFee = 0.0;

    @DecimalMin(value = "0.0", message = "Gateway fee cannot be negative")
    private Double gatewayFee = 0.0;

    @DecimalMin(value = "0.0", message = "Tax amount cannot be negative")
    private Double taxAmount = 0.0;

    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    private Double discountAmount = 0.0;

    @DecimalMin(value = "0.0", message = "Net amount cannot be negative")
    private Double netAmount;

    private String currency = "INR";

    private String description;

    private String failureReason;

    private String refundReason;

    @DecimalMin(value = "0.0", message = "Refunded amount cannot be negative")
    private Double refundedAmount = 0.0;

    private LocalDateTime refundedAt;

    private String refundTransactionId;

    // For group orders - split payment details
    private Boolean isGroupPayment = false;
    
    private String groupOrderId;
    
    private List<SplitPayment> splitPayments;

    // Gateway specific data
    private Map<String, Object> gatewayMetadata;

    // Webhook data
    private Map<String, Object> webhookData;

    private LocalDateTime processedAt;

    private LocalDateTime failedAt;

    private Integer retryCount = 0;

    private LocalDateTime nextRetryAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Inner class for split payments
    public static class SplitPayment {
        @NotBlank(message = "User ID is required")
        private String userId;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private Double amount;

        @NotNull(message = "Status is required")
        private PaymentStatus status = PaymentStatus.PENDING;

        private String transactionId;

        private String gatewayPaymentId;

        private LocalDateTime processedAt;

        // Constructors
        public SplitPayment() {}

        public SplitPayment(String userId, Double amount) {
            this.userId = userId;
            this.amount = amount;
        }

        // Getters and Setters
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

        public PaymentStatus getStatus() {
            return status;
        }

        public void setStatus(PaymentStatus status) {
            this.status = status;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getGatewayPaymentId() {
            return gatewayPaymentId;
        }

        public void setGatewayPaymentId(String gatewayPaymentId) {
            this.gatewayPaymentId = gatewayPaymentId;
        }

        public LocalDateTime getProcessedAt() {
            return processedAt;
        }

        public void setProcessedAt(LocalDateTime processedAt) {
            this.processedAt = processedAt;
        }
    }

    // Constructors
    public Payment() {}

    public Payment(String orderId, String userId, PaymentMethod paymentMethod, Double amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.netAmount = amount;
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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getGatewayPaymentId() {
        return gatewayPaymentId;
    }

    public void setGatewayPaymentId(String gatewayPaymentId) {
        this.gatewayPaymentId = gatewayPaymentId;
    }

    public String getGatewayOrderId() {
        return gatewayOrderId;
    }

    public void setGatewayOrderId(String gatewayOrderId) {
        this.gatewayOrderId = gatewayOrderId;
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

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Double netAmount) {
        this.netAmount = netAmount;
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

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public Double getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(Double refundedAmount) {
        this.refundedAmount = refundedAmount;
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

    public Boolean getIsGroupPayment() {
        return isGroupPayment;
    }

    public void setIsGroupPayment(Boolean isGroupPayment) {
        this.isGroupPayment = isGroupPayment;
    }

    public String getGroupOrderId() {
        return groupOrderId;
    }

    public void setGroupOrderId(String groupOrderId) {
        this.groupOrderId = groupOrderId;
    }

    public List<SplitPayment> getSplitPayments() {
        return splitPayments;
    }

    public void setSplitPayments(List<SplitPayment> splitPayments) {
        this.splitPayments = splitPayments;
    }

    public Map<String, Object> getGatewayMetadata() {
        return gatewayMetadata;
    }

    public void setGatewayMetadata(Map<String, Object> gatewayMetadata) {
        this.gatewayMetadata = gatewayMetadata;
    }

    public Map<String, Object> getWebhookData() {
        return webhookData;
    }

    public void setWebhookData(Map<String, Object> webhookData) {
        this.webhookData = webhookData;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getNextRetryAt() {
        return nextRetryAt;
    }

    public void setNextRetryAt(LocalDateTime nextRetryAt) {
        this.nextRetryAt = nextRetryAt;
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