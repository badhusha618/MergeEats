package com.mergeeats.common.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Document(collection = "restaurants")
public class Restaurant {
    
    @Id
    private String restaurantId;
    
    @NotBlank(message = "Restaurant name is required")
    @Size(max = 200, message = "Restaurant name cannot exceed 200 characters")
    @Indexed
    private String name;
    
    @NotBlank(message = "Owner ID is required")
    @Indexed
    private String ownerId;
    
    private String description;
    
    private Address address;
    
    @Indexed
    private String phoneNumber;
    
    private String email;
    
    private String imageUrl;
    
    private List<String> cuisineTypes;
    
    @Min(value = 0, message = "Rating cannot be negative")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Double rating = 0.0;
    
    private Integer totalReviews = 0;
    
    private LocalTime openingTime;
    
    private LocalTime closingTime;
    
    private boolean isOpen = true;
    
    private boolean isActive = true;
    
    private boolean acceptsOnlineOrders = true;
    
    // Delivery settings
    private Double deliveryRadius; // in kilometers
    
    private Integer minimumOrderAmount;
    
    private Integer averagePreparationTime; // in minutes
    
    // Business details
    private String businessLicense;
    
    private String taxId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Constructors
    public Restaurant() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Restaurant(String name, String ownerId, Address address) {
        this();
        this.name = name;
        this.ownerId = ownerId;
        this.address = address;
    }
    
    // Getters and Setters
    public String getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    
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
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public List<String> getCuisineTypes() {
        return cuisineTypes;
    }
    
    public void setCuisineTypes(List<String> cuisineTypes) {
        this.cuisineTypes = cuisineTypes;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public Integer getTotalReviews() {
        return totalReviews;
    }
    
    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }
    
    public LocalTime getOpeningTime() {
        return openingTime;
    }
    
    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }
    
    public LocalTime getClosingTime() {
        return closingTime;
    }
    
    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }
    
    public boolean isOpen() {
        return isOpen;
    }
    
    public void setOpen(boolean open) {
        isOpen = open;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public boolean isAcceptsOnlineOrders() {
        return acceptsOnlineOrders;
    }
    
    public void setAcceptsOnlineOrders(boolean acceptsOnlineOrders) {
        this.acceptsOnlineOrders = acceptsOnlineOrders;
    }
    
    public Double getDeliveryRadius() {
        return deliveryRadius;
    }
    
    public void setDeliveryRadius(Double deliveryRadius) {
        this.deliveryRadius = deliveryRadius;
    }
    
    public Integer getMinimumOrderAmount() {
        return minimumOrderAmount;
    }
    
    public void setMinimumOrderAmount(Integer minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }
    
    public Integer getAveragePreparationTime() {
        return averagePreparationTime;
    }
    
    public void setAveragePreparationTime(Integer averagePreparationTime) {
        this.averagePreparationTime = averagePreparationTime;
    }
    
    public String getBusinessLicense() {
        return businessLicense;
    }
    
    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }
    
    public String getTaxId() {
        return taxId;
    }
    
    public void setTaxId(String taxId) {
        this.taxId = taxId;
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