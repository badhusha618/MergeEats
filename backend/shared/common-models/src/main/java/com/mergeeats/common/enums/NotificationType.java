package com.mergeeats.common.enums;

public enum NotificationType {
    ORDER_CONFIRMATION("Order Confirmation", 1),
    ORDER_PREPARING("Order Being Prepared", 2),
    ORDER_READY("Order Ready for Pickup", 1),
    ORDER_PICKED_UP("Order Picked Up", 2),
    ORDER_OUT_FOR_DELIVERY("Order Out for Delivery", 1),
    ORDER_DELIVERED("Order Delivered", 1),
    ORDER_CANCELLED("Order Cancelled", 1),
    
    PAYMENT_SUCCESS("Payment Successful", 1),
    PAYMENT_FAILED("Payment Failed", 1),
    PAYMENT_REFUND("Payment Refunded", 2),
    
    DELIVERY_ASSIGNED("Delivery Assigned", 3),
    DELIVERY_ACCEPTED("Delivery Accepted", 2),
    DELIVERY_PARTNER_ARRIVING("Delivery Partner Arriving", 1),
    DELIVERY_DELAYED("Delivery Delayed", 1),
    
    RESTAURANT_NEW_ORDER("New Order Received", 1),
    RESTAURANT_ORDER_CANCELLED("Order Cancelled", 2),
    
    PROMOTIONAL("Promotional Offer", 4),
    SYSTEM_MAINTENANCE("System Maintenance", 2),
    ACCOUNT_UPDATE("Account Update", 3),
    
    GROUP_ORDER_INVITE("Group Order Invitation", 2),
    GROUP_ORDER_UPDATE("Group Order Update", 3),
    GROUP_ORDER_FINALIZED("Group Order Finalized", 1);

    private final String displayName;
    private final Integer defaultPriority; // 1 = Highest, 5 = Lowest

    NotificationType(String displayName, Integer defaultPriority) {
        this.displayName = displayName;
        this.defaultPriority = defaultPriority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Integer getDefaultPriority() {
        return defaultPriority;
    }

    public boolean isOrderRelated() {
        return name().startsWith("ORDER_");
    }

    public boolean isPaymentRelated() {
        return name().startsWith("PAYMENT_");
    }

    public boolean isDeliveryRelated() {
        return name().startsWith("DELIVERY_");
    }

    public boolean isRestaurantRelated() {
        return name().startsWith("RESTAURANT_");
    }

    public boolean isGroupOrderRelated() {
        return name().startsWith("GROUP_ORDER_");
    }

    public boolean isHighPriority() {
        return defaultPriority <= 2;
    }

    public boolean requiresImmediateDelivery() {
        switch (this) {
            case ORDER_CONFIRMATION:
            case ORDER_DELIVERED:
            case ORDER_CANCELLED:
            case PAYMENT_SUCCESS:
            case PAYMENT_FAILED:
            case DELIVERY_PARTNER_ARRIVING:
            case RESTAURANT_NEW_ORDER:
                return true;
            default:
                return false;
        }
    }
}