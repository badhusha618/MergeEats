package com.mergeeats.paymentservice.dto;

import jakarta.validation.constraints.*;

public class RefundRequest {

    @NotBlank(message = "Payment ID is required")
    private String paymentId;

    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    private Double refundAmount;

    @NotBlank(message = "Refund reason is required")
    @Size(min = 10, max = 500, message = "Refund reason must be between 10 and 500 characters")
    private String refundReason;

    private Boolean isPartialRefund = false;

    // Constructors
    public RefundRequest() {}

    public RefundRequest(String paymentId, Double refundAmount, String refundReason) {
        this.paymentId = paymentId;
        this.refundAmount = refundAmount;
        this.refundReason = refundReason;
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public Boolean getIsPartialRefund() {
        return isPartialRefund;
    }

    public void setIsPartialRefund(Boolean isPartialRefund) {
        this.isPartialRefund = isPartialRefund;
    }
}