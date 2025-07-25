package com.mergeeats.common.enums;

public enum PaymentStatus {
    PENDING("Payment is pending"),
    PROCESSING("Payment is being processed"),
    COMPLETED("Payment completed successfully"),
    FAILED("Payment failed"),
    CANCELLED("Payment cancelled"),
    REFUNDED("Payment refunded"),
    PARTIALLY_REFUNDED("Payment partially refunded");
    
    private final String description;
    
    PaymentStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isCompleted() {
        return this == COMPLETED;
    }
    
    public boolean isFailed() {
        return this == FAILED || this == CANCELLED;
    }
    
    public boolean canBeRefunded() {
        return this == COMPLETED;
    }
}