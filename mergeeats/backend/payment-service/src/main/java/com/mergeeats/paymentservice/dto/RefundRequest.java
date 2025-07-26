package com.mergeeats.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class RefundRequest {

    @Schema(example = "payment_123456789")
    @NotBlank(message = "Payment ID is required")
    private String paymentId;

    @Schema(example = "29.99")
    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    private Double amount;

    @Schema(example = "Customer requested refund due to wrong order")
    @NotBlank(message = "Refund reason is required")
    @Size(max = 500, message = "Refund reason cannot exceed 500 characters")
    private String reason;

    // Constructors
    public RefundRequest() {}

    public RefundRequest(String paymentId, Double refundAmount, String reason) {
        this.paymentId = paymentId;
        this.amount = refundAmount;
        this.reason = reason;
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}