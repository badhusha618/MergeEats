package com.mergeeats.restaurantservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MenuItemRequest {
    
    @Schema(example = "Margherita Pizza")
    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Item name cannot exceed 100 characters")
    private String name;
    
    @Schema(example = "Classic tomato sauce with mozzarella cheese")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @Schema(example = "12.99")
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double price;
    
    @Schema(example = "Pizza")
    @NotBlank(message = "Category is required")
    private String category;
    
    @Schema(example = "true")
    private Boolean isAvailable = true;

    @Schema(example = "false")
    private Boolean isVegetarian = false;

    @Schema(example = "false")
    private Boolean isVegan = false;

    @Schema(example = "false")
    private Boolean isGlutenFree = false;
    
    private String imageUrl;

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getIsVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
} 