package com.mergeeats.common.enums;

/**
 * Enumeration representing different types of payments in the MergeEats system
 */
public enum PaymentType {
    
    ORDER_PAYMENT("Payment for an order"),
    REFUND("Refund payment"),
    TIP("Tip payment"),
    DELIVERY_FEE("Delivery fee payment"),
    PLATFORM_FEE("Platform fee payment"),
    TAX("Tax payment"),
    DISCOUNT("Discount adjustment"),
    SUBSCRIPTION("Subscription payment"),
    WALLET_TOPUP("Wallet top-up payment"),
    WITHDRAWAL("Withdrawal payment");

    private final String description;

    PaymentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if this payment type is a customer-facing payment
     */
    public boolean isCustomerPayment() {
        return this == ORDER_PAYMENT || this == TIP || this == SUBSCRIPTION || this == WALLET_TOPUP;
    }

    /**
     * Check if this payment type is a system adjustment
     */
    public boolean isSystemAdjustment() {
        return this == REFUND || this == DISCOUNT;
    }

    /**
     * Check if this payment type is a fee
     */
    public boolean isFee() {
        return this == DELIVERY_FEE || this == PLATFORM_FEE || this == TAX;
    }
}