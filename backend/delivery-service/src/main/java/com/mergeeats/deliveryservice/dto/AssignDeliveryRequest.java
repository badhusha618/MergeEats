package com.mergeeats.deliveryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AssignDeliveryRequest {

    @NotBlank(message = "Delivery partner ID is required")
    private String deliveryPartnerId;

    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Invalid delivery partner phone number format")
    private String deliveryPartnerPhone;

    private String notes;

    // Constructors
    public AssignDeliveryRequest() {}

    public AssignDeliveryRequest(String deliveryPartnerId, String deliveryPartnerPhone) {
        this.deliveryPartnerId = deliveryPartnerId;
        this.deliveryPartnerPhone = deliveryPartnerPhone;
    }

    // Getters and Setters
    public String getDeliveryPartnerId() {
        return deliveryPartnerId;
    }

    public void setDeliveryPartnerId(String deliveryPartnerId) {
        this.deliveryPartnerId = deliveryPartnerId;
    }

    public String getDeliveryPartnerPhone() {
        return deliveryPartnerPhone;
    }

    public void setDeliveryPartnerPhone(String deliveryPartnerPhone) {
        this.deliveryPartnerPhone = deliveryPartnerPhone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}