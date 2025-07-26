package com.mergeeats.restaurantservice.dto;

import com.mergeeats.common.models.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;

public class CreateRestaurantRequest {
    
    @Schema(example = "Pizza Palace")
    @NotBlank(message = "Restaurant name is required")
    @Size(max = 100, message = "Restaurant name cannot exceed 100 characters")
    private String name;
    
    @Schema(example = "Authentic Italian pizza and pasta")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @Schema(example = "Italian")
    @NotBlank(message = "Cuisine type is required")
    private String cuisineType;
    
    @Schema(example = "123 Main St, City, State 12345")
    @NotBlank(message = "Address is required")
    private String address;

    @Schema(example = "40.7128")
    private Double latitude;

    @Schema(example = "-74.0060")
    private Double longitude;

    @Schema(example = "+1-555-123-4567")
    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Schema(example = "info@pizzapalace.com")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(example = "https://www.pizzapalace.com")
    private String website;
    
    private Map<String, String> openingHours;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCuisine() {
        return cuisineType;
    }

    public void setCuisine(String cuisine) {
        this.cuisineType = cuisine;
    }

    public Address getAddress() {
        return new Address(address, latitude, longitude);
    }

    public void setAddress(Address address) {
        this.address = address.getAddress();
        this.latitude = address.getLatitude();
        this.longitude = address.getLongitude();
    }

    public String getPhone() {
        return phoneNumber;
    }

    public void setPhone(String phone) {
        this.phoneNumber = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(Map<String, String> openingHours) {
        this.openingHours = openingHours;
    }
} 