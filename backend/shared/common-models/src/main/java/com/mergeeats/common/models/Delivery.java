package com.mergeeats.common.models;

import com.mergeeats.common.enums.DeliveryStatus;
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

    @NotBlank(message = "Customer ID is required")
    @Indexed
    private String customerId;

    @NotBlank(message = "Restaurant ID is required")
    @Indexed
    private String restaurantId;

    @Indexed
    private String deliveryPartnerId;

    @NotNull(message = "Pickup address is required")
    private Address pickupAddress;

    @NotNull(message = "Delivery address is required")
    private Address deliveryAddress;

    @NotNull(message = "Status is required")
    private DeliveryStatus status = DeliveryStatus.PENDING;

    @DecimalMin(value = "0.0", message = "Distance cannot be negative")
    private Double distanceKm;

    @Min(value = 0, message = "Estimated time cannot be negative")
    private Integer estimatedTimeMinutes;

    @DecimalMin(value = "0.0", message = "Delivery fee cannot be negative")
    private Double deliveryFee;

    @DecimalMin(value = "0.0", message = "Partner earnings cannot be negative")
    private Double partnerEarnings;

    private String specialInstructions;

    private String customerPhoneNumber;

    private String restaurantPhoneNumber;

    private LocalDateTime scheduledPickupTime;

    private LocalDateTime actualPickupTime;

    private LocalDateTime estimatedDeliveryTime;

    private LocalDateTime actualDeliveryTime;

    private LocalDateTime assignedAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime pickedUpAt;

    private LocalDateTime deliveredAt;

    private LocalDateTime cancelledAt;

    private String cancellationReason;

    // For merged orders
    private Boolean isMergedDelivery = false;
    
    private List<String> mergedOrderIds;

    // Route optimization
    private List<Address> routeWaypoints;
    
    private String optimizedRoute;

    // Real-time tracking
    private Address currentLocation;
    
    private LocalDateTime lastLocationUpdate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Constructors
    public Delivery() {}

    public Delivery(String orderId, String customerId, String restaurantId, 
                   Address pickupAddress, Address deliveryAddress) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
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

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
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

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getRestaurantPhoneNumber() {
        return restaurantPhoneNumber;
    }

    public void setRestaurantPhoneNumber(String restaurantPhoneNumber) {
        this.restaurantPhoneNumber = restaurantPhoneNumber;
    }

    public LocalDateTime getScheduledPickupTime() {
        return scheduledPickupTime;
    }

    public void setScheduledPickupTime(LocalDateTime scheduledPickupTime) {
        this.scheduledPickupTime = scheduledPickupTime;
    }

    public LocalDateTime getActualPickupTime() {
        return actualPickupTime;
    }

    public void setActualPickupTime(LocalDateTime actualPickupTime) {
        this.actualPickupTime = actualPickupTime;
    }

    public LocalDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public LocalDateTime getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public void setActualDeliveryTime(LocalDateTime actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
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

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public Boolean getIsMergedDelivery() {
        return isMergedDelivery;
    }

    public void setIsMergedDelivery(Boolean isMergedDelivery) {
        this.isMergedDelivery = isMergedDelivery;
    }

    public List<String> getMergedOrderIds() {
        return mergedOrderIds;
    }

    public void setMergedOrderIds(List<String> mergedOrderIds) {
        this.mergedOrderIds = mergedOrderIds;
    }

    public List<Address> getRouteWaypoints() {
        return routeWaypoints;
    }

    public void setRouteWaypoints(List<Address> routeWaypoints) {
        this.routeWaypoints = routeWaypoints;
    }

    public String getOptimizedRoute() {
        return optimizedRoute;
    }

    public void setOptimizedRoute(String optimizedRoute) {
        this.optimizedRoute = optimizedRoute;
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