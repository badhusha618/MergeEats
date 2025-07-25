package com.mergeeats.common.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public class OrderItem {
    
    @NotBlank(message = "Menu item ID is required")
    private String menuItemId;
    
    @NotBlank(message = "Item name is required")
    private String itemName;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Total price is required")
    @Positive(message = "Total price must be positive")
    private BigDecimal totalPrice;
    
    private String specialInstructions;
    
    private List<String> customizations;
    
    private List<String> addOns;
    
    // Constructors
    public OrderItem() {}
    
    public OrderItem(String menuItemId, String itemName, Integer quantity, BigDecimal unitPrice) {
        this.menuItemId = menuItemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Getters and Setters
    public String getMenuItemId() {
        return menuItemId;
    }
    
    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (this.unitPrice != null) {
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        if (this.quantity != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    public List<String> getCustomizations() {
        return customizations;
    }
    
    public void setCustomizations(List<String> customizations) {
        this.customizations = customizations;
    }
    
    public List<String> getAddOns() {
        return addOns;
    }
    
    public void setAddOns(List<String> addOns) {
        this.addOns = addOns;
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "menuItemId='" + menuItemId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                ", specialInstructions='" + specialInstructions + '\'' +
                '}';
    }
}