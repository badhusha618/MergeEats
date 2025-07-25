package com.mergeeats.common.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "delivery_partners")
public class DeliveryPartner {

    @Id
    private String partnerId;

    @NotBlank(message = "User ID is required")
    @Indexed
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

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType; // BIKE, SCOOTER, CAR, BICYCLE

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    private Address currentLocation;

    @NotNull(message = "Availability status is required")
    private Boolean isAvailable = false;

    @NotNull(message = "Online status is required")
    private Boolean isOnline = false;

    @NotNull(message = "Active status is required")
    private Boolean isActive = true;

    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private Double rating = 0.0;

    @Min(value = 0, message = "Total deliveries cannot be negative")
    private Integer totalDeliveries = 0;

    @Min(value = 0, message = "Completed deliveries cannot be negative")
    private Integer completedDeliveries = 0;

    @Min(value = 0, message = "Cancelled deliveries cannot be negative")
    private Integer cancelledDeliveries = 0;

    private List<String> currentOrderIds;

    @DecimalMin(value = "0.0", message = "Earnings cannot be negative")
    private Double totalEarnings = 0.0;

    @DecimalMin(value = "0.0", message = "Today's earnings cannot be negative")
    private Double todayEarnings = 0.0;

    private LocalDateTime lastLocationUpdate;

    private LocalDateTime lastActiveTime;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Constructors
    public DeliveryPartner() {}

    public DeliveryPartner(String userId, String fullName, String email, String phoneNumber, 
                          String vehicleType, String vehicleNumber, String licenseNumber) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
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

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
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

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public List<String> getCurrentOrderIds() {
        return currentOrderIds;
    }

    public void setCurrentOrderIds(List<String> currentOrderIds) {
        this.currentOrderIds = currentOrderIds;
    }

    public Double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(Double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public Double getTodayEarnings() {
        return todayEarnings;
    }

    public void setTodayEarnings(Double todayEarnings) {
        this.todayEarnings = todayEarnings;
    }

    public LocalDateTime getLastLocationUpdate() {
        return lastLocationUpdate;
    }

    public void setLastLocationUpdate(LocalDateTime lastLocationUpdate) {
        this.lastLocationUpdate = lastLocationUpdate;
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
}