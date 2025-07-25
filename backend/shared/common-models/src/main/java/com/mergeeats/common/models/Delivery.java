package com.mergeeats.common.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "deliveries")
public class Delivery {

    @Id
    private String deliveryId;

    @NotBlank(message = "Order ID is required")
    @Indexed
    private String orderId;

    @NotBlank(message = "Delivery partner ID is required")
    @Indexed
    private String deliveryPartnerId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    @NotNull(message = "Pickup address is required")
    private Address pickupAddress;

    @NotNull(message = "Delivery address is required")
    private Address deliveryAddress;

    @NotBlank(message = "Status is required")
    private String status; // ASSIGNED, PICKED_UP, IN_TRANSIT, DELIVERED, CANCELLED

    @DecimalMin(value = "0.0", message = "Distance cannot be negative")
    private Double distanceKm;

    @DecimalMin(value = "0.0", message = "Delivery fee cannot be negative")
    private Double deliveryFee;

    @DecimalMin(value = "0.0", message = "Partner earnings cannot be negative")
    private Double partnerEarnings;

    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;

    @Min(value = 0, message = "Estimated time cannot be negative")
    private Integer estimatedDeliveryTimeMinutes;

    @Min(value = 0, message = "Actual time cannot be negative")
    private Integer actualDeliveryTimeMinutes;

    private List<String> mergedOrderIds; // For merged deliveries

    private String trackingCode;

    private String specialInstructions;

    private String cancellationReason;

    // Delivery tracking information
    private Address currentLocation;
    private LocalDateTime lastLocationUpdate;
    private List<DeliveryTrackingPoint> trackingPoints;

    // Customer feedback
    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private Double customerRating;

    private String customerFeedback;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Constructors
    public Delivery() {}

    public Delivery(String orderId, String deliveryPartnerId, String customerId, String restaurantId,
                   Address pickupAddress, Address deliveryAddress) {
        this.orderId = orderId;
        this.deliveryPartnerId = deliveryPartnerId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.status = "ASSIGNED";
    }

    // Inner class for tracking points
    public static class DeliveryTrackingPoint {
        private Double latitude;
        private Double longitude;
        private LocalDateTime timestamp;
        private String status;

        public DeliveryTrackingPoint() {}

        public DeliveryTrackingPoint(Double latitude, Double longitude, String status) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.status = status;
            this.timestamp = LocalDateTime.now();
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

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    // Getters and Setters
    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryPartnerId() {
        return deliveryPartnerId;
    }

    public void setDeliveryPartnerId(String deliveryPartnerId) {
        this.deliveryPartnerId = deliveryPartnerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Address getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(Address pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Double getPartnerEarnings() {
        return partnerEarnings;
    }

    public void setPartnerEarnings(Double partnerEarnings) {
        this.partnerEarnings = partnerEarnings;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(LocalDateTime pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public Integer getEstimatedDeliveryTimeMinutes() {
        return estimatedDeliveryTimeMinutes;
    }

    public void setEstimatedDeliveryTimeMinutes(Integer estimatedDeliveryTimeMinutes) {
        this.estimatedDeliveryTimeMinutes = estimatedDeliveryTimeMinutes;
    }

    public Integer getActualDeliveryTimeMinutes() {
        return actualDeliveryTimeMinutes;
    }

    public void setActualDeliveryTimeMinutes(Integer actualDeliveryTimeMinutes) {
        this.actualDeliveryTimeMinutes = actualDeliveryTimeMinutes;
    }

    public List<String> getMergedOrderIds() {
        return mergedOrderIds;
    }

    public void setMergedOrderIds(List<String> mergedOrderIds) {
        this.mergedOrderIds = mergedOrderIds;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public Address getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Address currentLocation) {
        this.currentLocation = currentLocation;
    }

    public LocalDateTime getLastLocationUpdate() {
        return lastLocationUpdate;
    }

    public void setLastLocationUpdate(LocalDateTime lastLocationUpdate) {
        this.lastLocationUpdate = lastLocationUpdate;
    }

    public List<DeliveryTrackingPoint> getTrackingPoints() {
        return trackingPoints;
    }

    public void setTrackingPoints(List<DeliveryTrackingPoint> trackingPoints) {
        this.trackingPoints = trackingPoints;
    }

    public Double getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(Double customerRating) {
        this.customerRating = customerRating;
    }

    public String getCustomerFeedback() {
        return customerFeedback;
    }

    public void setCustomerFeedback(String customerFeedback) {
        this.customerFeedback = customerFeedback;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}