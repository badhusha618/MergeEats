package com.mergeeats.common.enums;

public enum VehicleType {
    BICYCLE("Bicycle", 5.0, 15.0),
    BIKE("Motorcycle/Scooter", 25.0, 40.0),
    CAR("Car", 40.0, 60.0),
    VAN("Van", 60.0, 80.0);

    private final String displayName;
    private final Double minSpeedKmh;
    private final Double maxSpeedKmh;

    VehicleType(String displayName, Double minSpeedKmh, Double maxSpeedKmh) {
        this.displayName = displayName;
        this.minSpeedKmh = minSpeedKmh;
        this.maxSpeedKmh = maxSpeedKmh;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Double getMinSpeedKmh() {
        return minSpeedKmh;
    }

    public Double getMaxSpeedKmh() {
        return maxSpeedKmh;
    }

    public Double getAverageSpeedKmh() {
        return (minSpeedKmh + maxSpeedKmh) / 2;
    }

    public boolean isSuitableForDistance(Double distanceKm) {
        if (distanceKm == null) return true;
        
        switch (this) {
            case BICYCLE:
                return distanceKm <= 5.0;
            case BIKE:
                return distanceKm <= 15.0;
            case CAR:
                return distanceKm <= 25.0;
            case VAN:
                return true; // No distance limit
            default:
                return true;
        }
    }
}