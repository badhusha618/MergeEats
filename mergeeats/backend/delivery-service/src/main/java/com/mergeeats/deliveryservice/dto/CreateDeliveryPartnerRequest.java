package com.mergeeats.deliveryservice.dto;

import com.mergeeats.common.models.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class CreateDeliveryPartnerRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Vehicle type is required")
    @Pattern(regexp = "^(BIKE|SCOOTER|CAR|BICYCLE)$", message = "Vehicle type must be BIKE, SCOOTER, CAR, or BICYCLE")
    private String vehicleType;

    @NotBlank(message = "Vehicle number is required")
    @Size(min = 3, max = 20, message = "Vehicle number must be between 3 and 20 characters")
    private String vehicleNumber;

    @NotBlank(message = "License number is required")
    @Size(min = 5, max = 30, message = "License number must be between 5 and 30 characters")
    private String licenseNumber;

    @Valid
    private Address currentLocation;

    // Constructors
    public CreateDeliveryPartnerRequest() {}

    public CreateDeliveryPartnerRequest(String userId, String fullName, String email, String phoneNumber,
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
}