package com.mergeeats.common.enums;

public enum DeliveryStatus {
    PENDING("Delivery is pending assignment"),
    ASSIGNED("Delivery has been assigned to a partner"),
    ACCEPTED("Delivery partner has accepted the delivery"),
    PICKED_UP("Order has been picked up from restaurant"),
    IN_TRANSIT("Delivery is in transit to customer"),
    DELIVERED("Order has been delivered successfully"),
    CANCELLED("Delivery has been cancelled"),
    FAILED("Delivery attempt failed"),
    RETURNED("Order was returned to restaurant");

    private final String description;

    DeliveryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // Helper methods for status transitions
    public boolean canTransitionTo(DeliveryStatus newStatus) {
        switch (this) {
            case PENDING:
                return newStatus == ASSIGNED || newStatus == CANCELLED;
            case ASSIGNED:
                return newStatus == ACCEPTED || newStatus == CANCELLED;
            case ACCEPTED:
                return newStatus == PICKED_UP || newStatus == CANCELLED;
            case PICKED_UP:
                return newStatus == IN_TRANSIT || newStatus == CANCELLED || newStatus == FAILED;
            case IN_TRANSIT:
                return newStatus == DELIVERED || newStatus == FAILED || newStatus == RETURNED;
            case DELIVERED:
            case CANCELLED:
            case FAILED:
            case RETURNED:
                return false; // Terminal states
            default:
                return false;
        }
    }

    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED || this == FAILED || this == RETURNED;
    }

    public boolean isActive() {
        return this == ASSIGNED || this == ACCEPTED || this == PICKED_UP || this == IN_TRANSIT;
    }
}