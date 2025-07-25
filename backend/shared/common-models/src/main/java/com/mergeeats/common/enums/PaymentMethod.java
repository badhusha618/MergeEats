package com.mergeeats.common.enums;

public enum PaymentMethod {
    CREDIT_CARD("Credit Card", true, true),
    DEBIT_CARD("Debit Card", true, true),
    UPI("UPI (Unified Payments Interface)", true, false),
    DIGITAL_WALLET("Digital Wallet", true, false),
    NET_BANKING("Net Banking", true, false),
    CASH_ON_DELIVERY("Cash on Delivery", false, false),
    BANK_TRANSFER("Bank Transfer", true, true);

    private final String displayName;
    private final boolean requiresOnlineProcessing;
    private final boolean supportsRefunds;

    PaymentMethod(String displayName, boolean requiresOnlineProcessing, boolean supportsRefunds) {
        this.displayName = displayName;
        this.requiresOnlineProcessing = requiresOnlineProcessing;
        this.supportsRefunds = supportsRefunds;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean requiresOnlineProcessing() {
        return requiresOnlineProcessing;
    }

    public boolean supportsRefunds() {
        return supportsRefunds;
    }

    public boolean isCardPayment() {
        return this == CREDIT_CARD || this == DEBIT_CARD;
    }

    public boolean isDigitalPayment() {
        return this != CASH_ON_DELIVERY;
    }

    public boolean requiresGateway() {
        return requiresOnlineProcessing;
    }

    public String getGatewayProvider() {
        switch (this) {
            case CREDIT_CARD:
            case DEBIT_CARD:
                return "stripe"; // Default to Stripe for cards
            case UPI:
            case DIGITAL_WALLET:
            case NET_BANKING:
                return "razorpay"; // Default to Razorpay for Indian payment methods
            case BANK_TRANSFER:
                return "stripe";
            case CASH_ON_DELIVERY:
            default:
                return null;
        }
    }
}