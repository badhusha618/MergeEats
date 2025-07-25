package com.mergeeats.common.enums;

public enum DeliveryPartnerStatus {
    OFFLINE("Partner is offline"),
    ONLINE("Partner is online and available"),
    BUSY("Partner is busy with deliveries"),
    ON_BREAK("Partner is on break"),
    INACTIVE("Partner account is inactive");

    private final String description;

    DeliveryPartnerStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailableForDelivery() {
        return this == ONLINE;
    }

    public boolean isActive() {
        return this != OFFLINE && this != INACTIVE;
    }
}