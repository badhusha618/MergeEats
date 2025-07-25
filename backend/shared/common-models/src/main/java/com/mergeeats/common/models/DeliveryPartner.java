package com.mergeeats.common.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "delivery_partners")
public class DeliveryPartner {

    @Id
    private String partnerId;

    @NotBlank(message = "User ID is required")
    @Indexed(unique = true)
    private String userId;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotBlank(message = "Vehicle registration number is required")
    private String vehicleRegistrationNumber;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @Valid
    private Address currentLocation;

    @NotNull(message = "Availability status is required")
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.OFFLINE;

    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private Double rating = 0.0;

    @Min(value = 0, message = "Total deliveries cannot be negative")
    private Integer totalDeliveries = 0;

    @Min(value = 0, message = "Completed deliveries cannot be negative")
    private Integer completedDeliveries = 0;

    @Min(value = 0, message = "Cancelled deliveries cannot be negative")
    private Integer cancelledDeliveries = 0;

    @DecimalMin(value = "0.0", message = "Total earnings cannot be negative")
    private Double totalEarnings = 0.0;

    @DecimalMin(value = "0.0", message = "Current balance cannot be negative")
    private Double currentBalance = 0.0;

    private List<String> activeOrderIds;

    @Min(value = 0, message = "Max concurrent orders cannot be negative")
    private Integer maxConcurrentOrders = 3;

    @DecimalMin(value = "0.0", message = "Delivery radius cannot be negative")
    private Double deliveryRadius = 10.0;

    private Boolean isActive = true;

    private Boolean isVerified = false;

    private LocalDateTime lastActiveTime;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Constructors
    public DeliveryPartner() {}

    public DeliveryPartner(String userId, String fullName, String email, String phoneNumber, 
                          VehicleType vehicleType, String vehicleRegistrationNumber, String licenseNumber) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.vehicleType = vehicleType;
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
        this.licenseNumber = licenseNumber;
    }

    // Getters and Setters
    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleRegistrationNumber() {
        return vehicleRegistrationNumber;
    }

    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Address getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Address currentLocation) {
        this.currentLocation = currentLocation;
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getTotalDeliveries() {
        return totalDeliveries;
    }

    public void setTotalDeliveries(Integer totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }

    public Integer getCompletedDeliveries() {
        return completedDeliveries;
    }

    public void setCompletedDeliveries(Integer completedDeliveries) {
        this.completedDeliveries = completedDeliveries;
    }

    public Integer getCancelledDeliveries() {
        return cancelledDeliveries;
    }

    public void setCancelledDeliveries(Integer cancelledDeliveries) {
        this.cancelledDeliveries = cancelledDeliveries;
    }

    public Double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(Double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public List<String> getActiveOrderIds() {
        return activeOrderIds;
    }

    public void setActiveOrderIds(List<String> activeOrderIds) {
        this.activeOrderIds = activeOrderIds;
    }

    public Integer getMaxConcurrentOrders() {
        return maxConcurrentOrders;
    }

    public void setMaxConcurrentOrders(Integer maxConcurrentOrders) {
        this.maxConcurrentOrders = maxConcurrentOrders;
    }

    public Double getDeliveryRadius() {
        return deliveryRadius;
    }

    public void setDeliveryRadius(Double deliveryRadius) {
        this.deliveryRadius = deliveryRadius;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public LocalDateTime getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(LocalDateTime lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
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
    public boolean isAvailable() {
        return availabilityStatus == AvailabilityStatus.AVAILABLE && isActive && isVerified;
    }

    public boolean canTakeMoreOrders() {
        return activeOrderIds != null && activeOrderIds.size() < maxConcurrentOrders;
    }

    public double getCompletionRate() {
        if (totalDeliveries == 0) return 0.0;
        return (double) completedDeliveries / totalDeliveries * 100;
    }

    public enum VehicleType {
        BICYCLE("Bicycle"),
        MOTORCYCLE("Motorcycle"),
        CAR("Car"),
        SCOOTER("Scooter");

        private final String displayName;

        VehicleType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum AvailabilityStatus {
        AVAILABLE("Available"),
        BUSY("Busy"),
        OFFLINE("Offline"),
        ON_BREAK("On Break");

        private final String displayName;

        AvailabilityStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}