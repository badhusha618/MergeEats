package com.mergeeats.restaurantservice.dto;

import com.mergeeats.common.models.Address;
import com.mergeeats.common.enums.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRequest {
    
    @NotBlank(message = "Restaurant name is required")
    @Size(min = 2, max = 100, message = "Restaurant name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Owner ID is required")
    private String ownerId;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @NotNull(message = "Address is required")
    @Valid
    private Address address;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @NotEmpty(message = "At least one cuisine type is required")
    private List<String> cuisineTypes;
    
    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;
    
    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;
    
    @DecimalMin(value = "0.0", message = "Minimum order amount cannot be negative")
    private Double minimumOrderAmount;
    
    @DecimalMin(value = "0.0", message = "Delivery fee cannot be negative")
    private Double deliveryFee;
    
    @DecimalMin(value = "1.0", message = "Delivery radius must be at least 1 km")
    @DecimalMax(value = "50.0", message = "Delivery radius cannot exceed 50 km")
    private Double deliveryRadius;
    
    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    @Max(value = 180, message = "Preparation time cannot exceed 180 minutes")
    private Integer averagePreparationTime;
    
    private Boolean acceptsOnlineOrders = true;
    private Boolean isActive = true;
    private Map<String, String> socialMediaLinks;
    private String website;
}