package com.mergeeats.deliveryservice.dto;

import jakarta.validation.constraints.*;

public class LocationUpdateRequest {

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    private String status; // Current delivery status

    // Constructors
    public LocationUpdateRequest() {}

    public LocationUpdateRequest(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationUpdateRequest(Double latitude, Double longitude, String status) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    // Getters and Setters
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}