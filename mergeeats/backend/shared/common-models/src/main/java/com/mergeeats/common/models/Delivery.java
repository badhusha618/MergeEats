package com.mergeeats.common.models;

import com.mergeeats.common.enums.DeliveryStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

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

    @NotBlank(message = "Customer ID is required")
    @Indexed
    private String customerId;

    @NotBlank(message = "Restaurant ID is required")
    @Indexed
    private String restaurantId;

    @Indexed
    private String deliveryPartnerId;

    @NotNull(message = "Delivery status is required")
    @Indexed
    private DeliveryStatus status = DeliveryStatus.PENDING;

    @NotNull(message = "Pickup address is required")
    private Address pickupAddress;

    @NotNull(message = "Delivery address is required")
    private Address deliveryAddress;

    // Current location for real-time tracking (GeoJSON Point)
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private double[] currentLocation; // [longitude, latitude]

    @NotNull(message = "Order total is required")
    @DecimalMin(value = "0.0", message = "Order total cannot be negative")
    private Double orderTotal;

    @DecimalMin(value = "0.0", message = "Delivery fee cannot be negative")
    private Double deliveryFee = 0.0;

    private Double estimatedDistance; // in kilometers

    @NotNull(message = "Scheduled pickup time is required")
    private LocalDateTime scheduledPickupTime;

    @NotNull(message = "Estimated delivery time is required")
    private LocalDateTime estimatedDeliveryTime;

    private LocalDateTime actualPickupTime;

    private LocalDateTime actualDeliveryTime;

    // Tracking information
    private String trackingNumber;

    private List<DeliveryUpdate> trackingUpdates;

    // Route optimization data
    private List<double[]> optimizedRoute; // Array of [longitude, latitude] points

    private Double routeDistance; // Total route distance in kilometers

    private Integer estimatedDuration; // Estimated duration in minutes

    // Batch delivery information
    private Boolean isBatchDelivery = false;

    private String batchId;

    private List<String> batchOrderIds;

    // Special instructions
    private String deliveryInstructions;

    private String customerNotes;

    // Contact information
    private String customerPhone;

    private String restaurantPhone;

    private String deliveryPartnerPhone;

    // Payment information
    private Boolean isPaid = false;

    private String paymentMethod;

    private Boolean requiresSignature = false;

    // Rating and feedback
    private Integer customerRating; // 1-5 stars

    private String customerFeedback;

    private Integer restaurantRating; // 1-5 stars

    private String restaurantFeedback;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Constructors
    public Delivery() {}

    public Delivery(String orderId, String customerId, String restaurantId, 
                   Address pickupAddress, Address deliveryAddress, Double orderTotal,
                   LocalDateTime scheduledPickupTime, LocalDateTime estimatedDeliveryTime) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.orderTotal = orderTotal;
        this.scheduledPickupTime = scheduledPickupTime;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.status = DeliveryStatus.PENDING;
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

    public String getDeliveryPartnerId() {
        return deliveryPartnerId;
    }

    public void setDeliveryPartnerId(String deliveryPartnerId) {
        this.deliveryPartnerId = deliveryPartnerId;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
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

    public double[] getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(double[] currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Double getEstimatedDistance() {
        return estimatedDistance;
    }

    public void setEstimatedDistance(Double estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }

    public LocalDateTime getScheduledPickupTime() {
        return scheduledPickupTime;
    }

    public void setScheduledPickupTime(LocalDateTime scheduledPickupTime) {
        this.scheduledPickupTime = scheduledPickupTime;
    }

    public LocalDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public LocalDateTime getActualPickupTime() {
        return actualPickupTime;
    }

    public void setActualPickupTime(LocalDateTime actualPickupTime) {
        this.actualPickupTime = actualPickupTime;
    }

    public LocalDateTime getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public void setActualDeliveryTime(LocalDateTime actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public List<DeliveryUpdate> getTrackingUpdates() {
        return trackingUpdates;
    }

    public void setTrackingUpdates(List<DeliveryUpdate> trackingUpdates) {
        this.trackingUpdates = trackingUpdates;
    }

    public List<double[]> getOptimizedRoute() {
        return optimizedRoute;
    }

    public void setOptimizedRoute(List<double[]> optimizedRoute) {
        this.optimizedRoute = optimizedRoute;
    }

    public Double getRouteDistance() {
        return routeDistance;
    }

    public void setRouteDistance(Double routeDistance) {
        this.routeDistance = routeDistance;
    }

    public Integer getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(Integer estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public Boolean getIsBatchDelivery() {
        return isBatchDelivery;
    }

    public void setIsBatchDelivery(Boolean isBatchDelivery) {
        this.isBatchDelivery = isBatchDelivery;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public List<String> getBatchOrderIds() {
        return batchOrderIds;
    }

    public void setBatchOrderIds(List<String> batchOrderIds) {
        this.batchOrderIds = batchOrderIds;
    }

    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }

    public String getCustomerNotes() {
        return customerNotes;
    }

    public void setCustomerNotes(String customerNotes) {
        this.customerNotes = customerNotes;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }

    public String getDeliveryPartnerPhone() {
        return deliveryPartnerPhone;
    }

    public void setDeliveryPartnerPhone(String deliveryPartnerPhone) {
        this.deliveryPartnerPhone = deliveryPartnerPhone;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Boolean getRequiresSignature() {
        return requiresSignature;
    }

    public void setRequiresSignature(Boolean requiresSignature) {
        this.requiresSignature = requiresSignature;
    }

    public Integer getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(Integer customerRating) {
        this.customerRating = customerRating;
    }

    public String getCustomerFeedback() {
        return customerFeedback;
    }

    public void setCustomerFeedback(String customerFeedback) {
        this.customerFeedback = customerFeedback;
    }

    public Integer getRestaurantRating() {
        return restaurantRating;
    }

    public void setRestaurantRating(Integer restaurantRating) {
        this.restaurantRating = restaurantRating;
    }

    public String getRestaurantFeedback() {
        return restaurantFeedback;
    }

    public void setRestaurantFeedback(String restaurantFeedback) {
        this.restaurantFeedback = restaurantFeedback;
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

    // Helper methods
    public boolean isActive() {
        return status == DeliveryStatus.ASSIGNED || 
               status == DeliveryStatus.PICKED_UP || 
               status == DeliveryStatus.IN_TRANSIT;
    }

    public boolean isCompleted() {
        return status == DeliveryStatus.DELIVERED;
    }

    public boolean isCancelled() {
        return status == DeliveryStatus.CANCELLED;
    }

    public void updateLocation(double longitude, double latitude) {
        this.currentLocation = new double[]{longitude, latitude};
    }
}