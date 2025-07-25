package com.mergeeats.deliveryservice.controller;

import com.mergeeats.common.models.DeliveryPartner;
import com.mergeeats.common.models.DeliveryPartner.AvailabilityStatus;
import com.mergeeats.common.models.DeliveryPartner.VehicleType;
import com.mergeeats.common.models.Address;
import com.mergeeats.deliveryservice.service.DeliveryPartnerService;
import com.mergeeats.deliveryservice.dto.RegisterDeliveryPartnerRequest;
import com.mergeeats.deliveryservice.dto.UpdateLocationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/partners")
@Tag(name = "Delivery Partners", description = "Delivery partner management operations")
public class DeliveryPartnerController {

    @Autowired
    private DeliveryPartnerService deliveryPartnerService;

    // Registration and Profile Management
    @PostMapping("/register")
    @Operation(summary = "Register a new delivery partner", description = "Register a new delivery partner in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Partner registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Partner already exists")
    })
    public ResponseEntity<?> registerPartner(@Valid @RequestBody RegisterDeliveryPartnerRequest request) {
        try {
            // Convert request to DeliveryPartner entity
            DeliveryPartner partner = new DeliveryPartner(
                request.getUserId(),
                request.getFullName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getVehicleType(),
                request.getVehicleRegistrationNumber(),
                request.getLicenseNumber()
            );
            
            partner.setCurrentLocation(request.getCurrentLocation());
            partner.setMaxConcurrentOrders(request.getMaxConcurrentOrders());
            partner.setDeliveryRadius(request.getDeliveryRadius());

            DeliveryPartner registeredPartner = deliveryPartnerService.registerPartner(partner);
            return new ResponseEntity<>(registeredPartner, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Registration failed", e.getMessage()));
        }
    }

    @GetMapping("/{partnerId}")
    @Operation(summary = "Get delivery partner by ID", description = "Retrieve delivery partner details by partner ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner found"),
        @ApiResponse(responseCode = "404", description = "Partner not found")
    })
    public ResponseEntity<?> getPartnerById(
            @Parameter(description = "Partner ID", required = true) @PathVariable String partnerId) {
        Optional<DeliveryPartner> partner = deliveryPartnerService.getPartnerById(partnerId);
        if (partner.isPresent()) {
            return ResponseEntity.ok(partner.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get delivery partner by user ID", description = "Retrieve delivery partner details by user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner found"),
        @ApiResponse(responseCode = "404", description = "Partner not found")
    })
    public ResponseEntity<?> getPartnerByUserId(
            @Parameter(description = "User ID", required = true) @PathVariable String userId) {
        Optional<DeliveryPartner> partner = deliveryPartnerService.getPartnerByUserId(userId);
        if (partner.isPresent()) {
            return ResponseEntity.ok(partner.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{partnerId}")
    @Operation(summary = "Update delivery partner", description = "Update delivery partner profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner updated successfully"),
        @ApiResponse(responseCode = "404", description = "Partner not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> updatePartner(
            @Parameter(description = "Partner ID", required = true) @PathVariable String partnerId,
            @RequestBody DeliveryPartner updatedPartner) {
        try {
            DeliveryPartner partner = deliveryPartnerService.updatePartner(partnerId, updatedPartner);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Update failed", e.getMessage()));
        }
    }

    // Availability Management
    @PutMapping("/{partnerId}/status")
    @Operation(summary = "Update partner availability status", description = "Update the availability status of a delivery partner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Partner not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status")
    })
    public ResponseEntity<?> updateAvailabilityStatus(
            @Parameter(description = "Partner ID", required = true) @PathVariable String partnerId,
            @Parameter(description = "New availability status", required = true) @RequestParam AvailabilityStatus status) {
        try {
            DeliveryPartner partner = deliveryPartnerService.updateAvailabilityStatus(partnerId, status);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Status update failed", e.getMessage()));
        }
    }

    @PutMapping("/{partnerId}/location")
    @Operation(summary = "Update partner location", description = "Update the current location of a delivery partner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Location updated successfully"),
        @ApiResponse(responseCode = "404", description = "Partner not found"),
        @ApiResponse(responseCode = "400", description = "Invalid location data")
    })
    public ResponseEntity<?> updateLocation(
            @Parameter(description = "Partner ID", required = true) @PathVariable String partnerId,
            @Valid @RequestBody UpdateLocationRequest locationRequest) {
        try {
            // Convert request to Address
            Address address = new Address();
            address.setLatitude(locationRequest.getLatitude());
            address.setLongitude(locationRequest.getLongitude());
            address.setStreet(locationRequest.getStreet());
            address.setCity(locationRequest.getCity());
            address.setState(locationRequest.getState());
            address.setPostalCode(locationRequest.getPostalCode());
            address.setCountry(locationRequest.getCountry());
            address.setLandmark(locationRequest.getLandmark());

            DeliveryPartner partner = deliveryPartnerService.updateLocation(partnerId, address);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Location update failed", e.getMessage()));
        }
    }

    // Partner Discovery
    @GetMapping("/available")
    @Operation(summary = "Get available partners", description = "Retrieve all available delivery partners")
    @ApiResponse(responseCode = "200", description = "Available partners retrieved successfully")
    public ResponseEntity<List<DeliveryPartner>> getAvailablePartners() {
        List<DeliveryPartner> partners = deliveryPartnerService.getAvailablePartners();
        return ResponseEntity.ok(partners);
    }

    @GetMapping("/search")
    @Operation(summary = "Search partners in area", description = "Find delivery partners within a specified radius")
    @ApiResponse(responseCode = "200", description = "Partners found successfully")
    public ResponseEntity<List<DeliveryPartner>> getPartnersInArea(
            @Parameter(description = "Latitude", required = true) @RequestParam double latitude,
            @Parameter(description = "Longitude", required = true) @RequestParam double longitude,
            @Parameter(description = "Search radius in kilometers", required = false) @RequestParam(defaultValue = "10.0") double radiusKm) {
        List<DeliveryPartner> partners = deliveryPartnerService.getPartnersInArea(latitude, longitude, radiusKm);
        return ResponseEntity.ok(partners);
    }

    @GetMapping("/optimal")
    @Operation(summary = "Get optimal partners for order", description = "Find the best delivery partners for an order based on location and rating")
    @ApiResponse(responseCode = "200", description = "Optimal partners retrieved successfully")
    public ResponseEntity<List<DeliveryPartner>> getOptimalPartnersForOrder(
            @Parameter(description = "Latitude", required = true) @RequestParam double latitude,
            @Parameter(description = "Longitude", required = true) @RequestParam double longitude,
            @Parameter(description = "Search radius in kilometers", required = false) @RequestParam(defaultValue = "10.0") double radiusKm,
            @Parameter(description = "Minimum rating", required = false) @RequestParam(defaultValue = "3.0") double minRating) {
        List<DeliveryPartner> partners = deliveryPartnerService.getOptimalPartnersForOrder(latitude, longitude, radiusKm, minRating);
        return ResponseEntity.ok(partners);
    }

    @GetMapping("/vehicle/{vehicleType}")
    @Operation(summary = "Get partners by vehicle type", description = "Retrieve available partners with specific vehicle type")
    @ApiResponse(responseCode = "200", description = "Partners retrieved successfully")
    public ResponseEntity<List<DeliveryPartner>> getPartnersByVehicleType(
            @Parameter(description = "Vehicle type", required = true) @PathVariable VehicleType vehicleType) {
        List<DeliveryPartner> partners = deliveryPartnerService.getPartnersByVehicleType(vehicleType);
        return ResponseEntity.ok(partners);
    }

    // Order Management
    @PostMapping("/{partnerId}/orders/{orderId}/assign")
    @Operation(summary = "Assign order to partner", description = "Assign a delivery order to a specific partner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order assigned successfully"),
        @ApiResponse(responseCode = "404", description = "Partner not found"),
        @ApiResponse(responseCode = "400", description = "Assignment failed")
    })
    public ResponseEntity<?> assignOrder(
            @Parameter(description = "Partner ID", required = true) @PathVariable String partnerId,
            @Parameter(description = "Order ID", required = true) @PathVariable String orderId) {
        try {
            DeliveryPartner partner = deliveryPartnerService.assignOrder(partnerId, orderId);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Order assignment failed", e.getMessage()));
        }
    }

    @PostMapping("/{partnerId}/orders/{orderId}/complete")
    @Operation(summary = "Complete order", description = "Mark an order as completed by the delivery partner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order completed successfully"),
        @ApiResponse(responseCode = "404", description = "Partner or order not found"),
        @ApiResponse(responseCode = "400", description = "Completion failed")
    })
    public ResponseEntity<?> completeOrder(
            @Parameter(description = "Partner ID", required = true) @PathVariable String partnerId,
            @Parameter(description = "Order ID", required = true) @PathVariable String orderId) {
        try {
            DeliveryPartner partner = deliveryPartnerService.completeOrder(partnerId, orderId);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Order completion failed", e.getMessage()));
        }
    }

    @PostMapping("/{partnerId}/orders/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an assigned order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Partner or order not found"),
        @ApiResponse(responseCode = "400", description = "Cancellation failed")
    })
    public ResponseEntity<?> cancelOrder(
            @Parameter(description = "Partner ID", required = true) @PathVariable String partnerId,
            @Parameter(description = "Order ID", required = true) @PathVariable String orderId,
            @Parameter(description = "Cancellation reason", required = false) @RequestParam(defaultValue = "No reason provided") String reason) {
        try {
            DeliveryPartner partner = deliveryPartnerService.cancelOrder(partnerId, orderId, reason);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Order cancellation failed", e.getMessage()));
        }
    }

    // Rating and Performance
    @PostMapping("/{partnerId}/rating")
    @Operation(summary = "Update partner rating", description = "Update the rating of a delivery partner after order completion")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rating updated successfully"),
        @ApiResponse(responseCode = "404", description = "Partner not found"),
        @ApiResponse(responseCode = "400", description = "Invalid rating")
    })
    public ResponseEntity<?> updateRating(
            @Parameter(description = "Partner ID", required = true) @PathVariable String partnerId,
            @Parameter(description = "New rating (1.0 to 5.0)", required = true) @RequestParam double rating,
            @Parameter(description = "Total number of ratings", required = false) @RequestParam(defaultValue = "1") int totalRatings) {
        try {
            if (rating < 1.0 || rating > 5.0) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid rating", "Rating must be between 1.0 and 5.0"));
            }
            DeliveryPartner partner = deliveryPartnerService.updateRating(partnerId, rating, totalRatings);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Rating update failed", e.getMessage()));
        }
    }

    @GetMapping("/top-performers")
    @Operation(summary = "Get top performing partners", description = "Retrieve the top performing delivery partners")
    @ApiResponse(responseCode = "200", description = "Top performers retrieved successfully")
    public ResponseEntity<List<DeliveryPartner>> getTopPerformingPartners(
            @Parameter(description = "Number of partners to retrieve", required = false) @RequestParam(defaultValue = "10") int limit) {
        List<DeliveryPartner> partners = deliveryPartnerService.getTopPerformingPartners(limit);
        return ResponseEntity.ok(partners);
    }

    // Verification and Management
    @PostMapping("/{partnerId}/verify")
    @Operation(summary = "Verify delivery partner", description = "Verify a delivery partner after document validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner verified successfully"),
        @ApiResponse(responseCode = "404", description = "Partner not found")
    })
    public ResponseEntity<?> verifyPartner(
            @Parameter(description = "Partner ID", required = true) @PathVariable String partnerId) {
        try {
            DeliveryPartner partner = deliveryPartnerService.verifyPartner(partnerId);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Verification failed", e.getMessage()));
        }
    }

    @PostMapping("/{partnerId}/deactivate")
    @Operation(summary = "Deactivate delivery partner", description = "Deactivate a delivery partner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "Partner not found")
    })
    public ResponseEntity<?> deactivatePartner(
            @Parameter(description = "Partner ID", required = true) @PathVariable String partnerId,
            @Parameter(description = "Deactivation reason", required = false) @RequestParam(defaultValue = "No reason provided") String reason) {
        try {
            DeliveryPartner partner = deliveryPartnerService.deactivatePartner(partnerId, reason);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Deactivation failed", e.getMessage()));
        }
    }

    // Health Check
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the delivery partner service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Delivery Partner Service is running");
    }

    // Error Response DTO
    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}