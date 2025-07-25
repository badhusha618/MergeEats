package com.mergeeats.restaurantservice.dto;

import com.mergeeats.common.models.Address;
import com.mergeeats.common.models.OperatingHours;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class CreateRestaurantRequest {
    
    @NotBlank(message = "Restaurant name is required")
    @Size(min = 2, max = 100, message = "Restaurant name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Owner ID is required")
    private String ownerId;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @Valid
    @NotNull(message = "Address is required")
    private Address address;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @NotEmpty(message = "At least one cuisine type is required")
    private List<String> cuisineTypes;
    
    @Valid
    private List<OperatingHours> operatingHours;
    
    @DecimalMin(value = "0.0", message = "Minimum order amount cannot be negative")
    private Double minimumOrderAmount;
    
    @DecimalMin(value = "0.0", message = "Delivery fee cannot be negative")
    private Double deliveryFee;
    
    @DecimalMin(value = "0.0", message = "Delivery radius cannot be negative")
    private Double deliveryRadius;
    
    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    @Max(value = 300, message = "Preparation time cannot exceed 300 minutes")
    private Integer averagePreparationTime;
    
    // Constructors
    public CreateRestaurantRequest() {}
    
    public CreateRestaurantRequest(String name, String ownerId, String description, Address address, 
                                 String phoneNumber, String email, List<String> cuisineTypes, 
                                 List<OperatingHours> operatingHours, Double minimumOrderAmount, 
                                 Double deliveryFee, Double deliveryRadius, Integer averagePreparationTime) {
        this.name = name;
        this.ownerId = ownerId;
        this.description = description;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.cuisineTypes = cuisineTypes;
        this.operatingHours = operatingHours;
        this.minimumOrderAmount = minimumOrderAmount;
        this.deliveryFee = deliveryFee;
        this.deliveryRadius = deliveryRadius;
        this.averagePreparationTime = averagePreparationTime;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Address getAddress() {
        return address;
    }
    
    public void setAddress(Address address) {
        this.address = address;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public List<String> getCuisineTypes() {
        return cuisineTypes;
    }
    
    public void setCuisineTypes(List<String> cuisineTypes) {
        this.cuisineTypes = cuisineTypes;
    }
    
    public List<OperatingHours> getOperatingHours() {
        return operatingHours;
    }
    
    public void setOperatingHours(List<OperatingHours> operatingHours) {
        this.operatingHours = operatingHours;
    }
    
    public Double getMinimumOrderAmount() {
        return minimumOrderAmount;
    }
    
    public void setMinimumOrderAmount(Double minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }
    
    public Double getDeliveryFee() {
        return deliveryFee;
    }
    
    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
    
    public Double getDeliveryRadius() {
        return deliveryRadius;
    }
    
    public void setDeliveryRadius(Double deliveryRadius) {
        this.deliveryRadius = deliveryRadius;
    }
    
    public Integer getAveragePreparationTime() {
        return averagePreparationTime;
    }
    
    public void setAveragePreparationTime(Integer averagePreparationTime) {
        this.averagePreparationTime = averagePreparationTime;
    }
}