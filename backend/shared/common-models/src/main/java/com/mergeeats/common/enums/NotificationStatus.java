package com.mergeeats.common.enums;

import java.util.Arrays;
import java.util.List;

public enum NotificationStatus {
    PENDING("Notification is pending to be sent"),
    SCHEDULED("Notification is scheduled for later delivery"),
    SENT("Notification has been sent"),
    DELIVERED("Notification has been delivered to the recipient"),
    READ("Notification has been read by the recipient"),
    FAILED("Notification delivery failed"),
    EXPIRED("Notification has expired"),
    CANCELLED("Notification has been cancelled");

    private final String description;

    NotificationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPending() {
        return this == PENDING || this == SCHEDULED;
    }

    public boolean isDelivered() {
        return this == DELIVERED || this == READ;
    }

    public boolean isFailed() {
        return this == FAILED || this == EXPIRED;
    }

    public boolean isTerminal() {
        return Arrays.asList(READ, FAILED, EXPIRED, CANCELLED).contains(this);
    }

    public boolean canTransitionTo(NotificationStatus newStatus) {
        switch (this) {
            case PENDING:
                return Arrays.asList(SCHEDULED, SENT, FAILED, CANCELLED).contains(newStatus);
            case SCHEDULED:
                return Arrays.asList(SENT, FAILED, EXPIRED, CANCELLED).contains(newStatus);
            case SENT:
                return Arrays.asList(DELIVERED, FAILED).contains(newStatus);
            case DELIVERED:
                return Arrays.asList(READ, FAILED).contains(newStatus);
            case READ:
            case FAILED:
            case EXPIRED:
            case CANCELLED:
                return false; // Terminal states
            default:
                return false;
        }
    }

    public static List<NotificationStatus> getActiveStatuses() {
        return Arrays.asList(PENDING, SCHEDULED, SENT, DELIVERED);
    }

    public static List<NotificationStatus> getFailedStatuses() {
        return Arrays.asList(FAILED, EXPIRED, CANCELLED);
    }

    public boolean requiresRetry() {
        return this == FAILED && !isTerminal();
    }
}