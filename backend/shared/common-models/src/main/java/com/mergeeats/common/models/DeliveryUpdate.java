package com.mergeeats.common.models;

import com.mergeeats.common.enums.DeliveryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class DeliveryUpdate {

    @NotNull(message = "Status is required")
    private DeliveryStatus status;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;

    // Location at the time of update (GeoJSON Point format)
    private double[] location; // [longitude, latitude]

    private String updatedBy; // User ID who made the update

    private String notes; // Additional notes about the update

    // Constructors
    public DeliveryUpdate() {}

    public DeliveryUpdate(DeliveryStatus status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public DeliveryUpdate(DeliveryStatus status, String message, LocalDateTime timestamp, 
                         double[] location, String updatedBy) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.location = location;
        this.updatedBy = updatedBy;
    }

    // Getters and Setters
    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Helper methods
    public void setLocation(double longitude, double latitude) {
        this.location = new double[]{longitude, latitude};
    }
}