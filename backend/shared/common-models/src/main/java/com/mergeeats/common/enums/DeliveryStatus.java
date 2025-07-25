package com.mergeeats.common.enums;

import java.util.Arrays;
import java.util.List;

public enum DeliveryStatus {
    PENDING("Delivery is pending assignment"),
    ASSIGNED("Delivery has been assigned to a partner"),
    ACCEPTED("Delivery partner has accepted the delivery"),
    PICKED_UP("Order has been picked up from restaurant"),
    IN_TRANSIT("Order is in transit to customer"),
    DELIVERED("Order has been delivered successfully"),
    CANCELLED("Delivery has been cancelled"),
    FAILED("Delivery attempt failed");

    private final String description;

    DeliveryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return Arrays.asList(PENDING, ASSIGNED, ACCEPTED, PICKED_UP, IN_TRANSIT).contains(this);
    }

    public boolean isCompleted() {
        return this == DELIVERED;
    }

    public boolean isCancelled() {
        return this == CANCELLED || this == FAILED;
    }

    public boolean canTransitionTo(DeliveryStatus newStatus) {
        switch (this) {
            case PENDING:
                return newStatus == ASSIGNED || newStatus == CANCELLED;
            case ASSIGNED:
                return newStatus == ACCEPTED || newStatus == CANCELLED;
            case ACCEPTED:
                return newStatus == PICKED_UP || newStatus == CANCELLED;
            case PICKED_UP:
                return newStatus == IN_TRANSIT || newStatus == CANCELLED;
            case IN_TRANSIT:
                return newStatus == DELIVERED || newStatus == FAILED;
            case DELIVERED:
            case CANCELLED:
            case FAILED:
                return false; // Terminal states
            default:
                return false;
        }
    }

    public static List<DeliveryStatus> getActiveStatuses() {
        return Arrays.asList(PENDING, ASSIGNED, ACCEPTED, PICKED_UP, IN_TRANSIT);
    }

    public static List<DeliveryStatus> getCompletedStatuses() {
        return Arrays.asList(DELIVERED, CANCELLED, FAILED);
    }
}