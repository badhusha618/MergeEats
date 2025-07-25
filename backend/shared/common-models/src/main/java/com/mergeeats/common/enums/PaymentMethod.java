package com.mergeeats.common.enums;

public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    UPI("UPI"),
    NET_BANKING("Net Banking"),
    DIGITAL_WALLET("Digital Wallet"),
    CASH_ON_DELIVERY("Cash on Delivery"),
    BANK_TRANSFER("Bank Transfer"),
    PAYPAL("PayPal"),
    APPLE_PAY("Apple Pay"),
    GOOGLE_PAY("Google Pay");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isOnlinePayment() {
        return this != CASH_ON_DELIVERY;
    }

    public boolean requiresGateway() {
        return this != CASH_ON_DELIVERY && this != BANK_TRANSFER;
    }
}