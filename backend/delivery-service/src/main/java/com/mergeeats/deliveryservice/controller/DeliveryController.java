package com.mergeeats.deliveryservice.controller;

import com.mergeeats.common.models.Delivery;
import com.mergeeats.common.models.DeliveryPartner;
import com.mergeeats.deliveryservice.dto.CreateDeliveryPartnerRequest;
import com.mergeeats.deliveryservice.dto.CreateDeliveryRequest;
import com.mergeeats.deliveryservice.dto.LocationUpdateRequest;
import com.mergeeats.deliveryservice.service.DeliveryPartnerService;
import com.mergeeats.deliveryservice.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
@Tag(name = "Delivery Management", description = "APIs for managing deliveries and delivery partners")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private DeliveryPartnerService deliveryPartnerService;

    // Delivery Partner Management APIs

    @PostMapping("/partners")
    @Operation(summary = "Create delivery partner", description = "Register a new delivery partner")
    @ApiResponse(responseCode = "201", description = "Delivery partner created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "409", description = "Partner already exists")
    public ResponseEntity<DeliveryPartner> createDeliveryPartner(
            @Valid @RequestBody CreateDeliveryPartnerRequest request) {
        try {
            DeliveryPartner partner = deliveryPartnerService.createDeliveryPartner(request);
            return new ResponseEntity<>(partner, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/partners/{partnerId}")
    @Operation(summary = "Get delivery partner", description = "Get delivery partner details by ID")
    @ApiResponse(responseCode = "200", description = "Partner found")
    @ApiResponse(responseCode = "404", description = "Partner not found")
    public ResponseEntity<DeliveryPartner> getDeliveryPartner(
            @Parameter(description = "Partner ID") @PathVariable String partnerId) {
        try {
            DeliveryPartner partner = deliveryPartnerService.getDeliveryPartnerById(partnerId);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/partners/user/{userId}")
    @Operation(summary = "Get delivery partner by user ID", description = "Get delivery partner details by user ID")
    @ApiResponse(responseCode = "200", description = "Partner found")
    @ApiResponse(responseCode = "404", description = "Partner not found")
    public ResponseEntity<DeliveryPartner> getDeliveryPartnerByUserId(
            @Parameter(description = "User ID") @PathVariable String userId) {
        try {
            DeliveryPartner partner = deliveryPartnerService.getDeliveryPartnerByUserId(userId);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/partners/{partnerId}/availability")
    @Operation(summary = "Update partner availability", description = "Update delivery partner availability status")
    @ApiResponse(responseCode = "200", description = "Availability updated")
    @ApiResponse(responseCode = "404", description = "Partner not found")
    public ResponseEntity<DeliveryPartner> updatePartnerAvailability(
            @Parameter(description = "Partner ID") @PathVariable String partnerId,
            @Parameter(description = "Availability status") @RequestParam Boolean isAvailable) {
        try {
            DeliveryPartner partner = deliveryPartnerService.updateAvailability(partnerId, isAvailable);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/partners/{partnerId}/online-status")
    @Operation(summary = "Update partner online status", description = "Update delivery partner online status")
    @ApiResponse(responseCode = "200", description = "Online status updated")
    @ApiResponse(responseCode = "404", description = "Partner not found")
    public ResponseEntity<DeliveryPartner> updatePartnerOnlineStatus(
            @Parameter(description = "Partner ID") @PathVariable String partnerId,
            @Parameter(description = "Online status") @RequestParam Boolean isOnline) {
        try {
            DeliveryPartner partner = deliveryPartnerService.updateOnlineStatus(partnerId, isOnline);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/partners/{partnerId}/location")
    @Operation(summary = "Update partner location", description = "Update delivery partner current location")
    @ApiResponse(responseCode = "200", description = "Location updated")
    @ApiResponse(responseCode = "404", description = "Partner not found")
    public ResponseEntity<DeliveryPartner> updatePartnerLocation(
            @Parameter(description = "Partner ID") @PathVariable String partnerId,
            @Valid @RequestBody LocationUpdateRequest request) {
        try {
            DeliveryPartner partner = deliveryPartnerService.updateLocation(partnerId, request);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/partners/available")
    @Operation(summary = "Get available partners", description = "Get all available delivery partners")
    @ApiResponse(responseCode = "200", description = "Available partners retrieved")
    public ResponseEntity<List<DeliveryPartner>> getAvailablePartners() {
        List<DeliveryPartner> partners = deliveryPartnerService.getAvailablePartners();
        return ResponseEntity.ok(partners);
    }

    @GetMapping("/partners/online")
    @Operation(summary = "Get online partners", description = "Get all online delivery partners")
    @ApiResponse(responseCode = "200", description = "Online partners retrieved")
    public ResponseEntity<List<DeliveryPartner>> getOnlinePartners() {
        List<DeliveryPartner> partners = deliveryPartnerService.getOnlinePartners();
        return ResponseEntity.ok(partners);
    }

    @GetMapping("/partners/area")
    @Operation(summary = "Find partners in area", description = "Find available delivery partners in specified area")
    @ApiResponse(responseCode = "200", description = "Partners found")
    public ResponseEntity<List<DeliveryPartner>> findPartnersInArea(
            @Parameter(description = "Center latitude") @RequestParam Double latitude,
            @Parameter(description = "Center longitude") @RequestParam Double longitude,
            @Parameter(description = "Search radius in km") @RequestParam(defaultValue = "10.0") Double radiusKm) {
        List<DeliveryPartner> partners = deliveryPartnerService.findAvailablePartnersInArea(latitude, longitude, radiusKm);
        return ResponseEntity.ok(partners);
    }

    @GetMapping("/partners/{partnerId}/statistics")
    @Operation(summary = "Get partner statistics", description = "Get delivery partner performance statistics")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved")
    @ApiResponse(responseCode = "404", description = "Partner not found")
    public ResponseEntity<Map<String, Object>> getPartnerStatistics(
            @Parameter(description = "Partner ID") @PathVariable String partnerId) {
        try {
            Map<String, Object> stats = deliveryPartnerService.getPartnerStatistics(partnerId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/partners/{partnerId}")
    @Operation(summary = "Deactivate partner", description = "Deactivate delivery partner")
    @ApiResponse(responseCode = "200", description = "Partner deactivated")
    @ApiResponse(responseCode = "404", description = "Partner not found")
    public ResponseEntity<DeliveryPartner> deactivatePartner(
            @Parameter(description = "Partner ID") @PathVariable String partnerId) {
        try {
            DeliveryPartner partner = deliveryPartnerService.deactivatePartner(partnerId);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Delivery Management APIs

    @PostMapping("/deliveries")
    @Operation(summary = "Create delivery", description = "Create a new delivery assignment")
    @ApiResponse(responseCode = "201", description = "Delivery created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "409", description = "Delivery already exists for order")
    public ResponseEntity<Delivery> createDelivery(@Valid @RequestBody CreateDeliveryRequest request) {
        try {
            Delivery delivery = deliveryService.createDelivery(request);
            return new ResponseEntity<>(delivery, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/deliveries/{deliveryId}")
    @Operation(summary = "Get delivery", description = "Get delivery details by ID")
    @ApiResponse(responseCode = "200", description = "Delivery found")
    @ApiResponse(responseCode = "404", description = "Delivery not found")
    public ResponseEntity<Delivery> getDelivery(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        try {
            Delivery delivery = deliveryService.getDeliveryById(deliveryId);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/deliveries/order/{orderId}")
    @Operation(summary = "Get delivery by order ID", description = "Get delivery details by order ID")
    @ApiResponse(responseCode = "200", description = "Delivery found")
    @ApiResponse(responseCode = "404", description = "Delivery not found")
    public ResponseEntity<Delivery> getDeliveryByOrderId(
            @Parameter(description = "Order ID") @PathVariable String orderId) {
        try {
            Delivery delivery = deliveryService.getDeliveryByOrderId(orderId);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/deliveries/tracking/{trackingCode}")
    @Operation(summary = "Track delivery", description = "Track delivery by tracking code")
    @ApiResponse(responseCode = "200", description = "Delivery found")
    @ApiResponse(responseCode = "404", description = "Delivery not found")
    public ResponseEntity<Delivery> trackDelivery(
            @Parameter(description = "Tracking code") @PathVariable String trackingCode) {
        try {
            Delivery delivery = deliveryService.getDeliveryByTrackingCode(trackingCode);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/deliveries/{deliveryId}/assign")
    @Operation(summary = "Assign delivery partner", description = "Assign delivery to a partner")
    @ApiResponse(responseCode = "200", description = "Partner assigned")
    @ApiResponse(responseCode = "404", description = "Delivery not found")
    @ApiResponse(responseCode = "400", description = "Assignment failed")
    public ResponseEntity<Delivery> assignDeliveryPartner(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        try {
            Delivery delivery = deliveryService.assignDeliveryPartner(deliveryId);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/deliveries/{deliveryId}/status")
    @Operation(summary = "Update delivery status", description = "Update delivery status")
    @ApiResponse(responseCode = "200", description = "Status updated")
    @ApiResponse(responseCode = "404", description = "Delivery not found")
    public ResponseEntity<Delivery> updateDeliveryStatus(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Parameter(description = "New status") @RequestParam String status) {
        try {
            Delivery delivery = deliveryService.updateDeliveryStatus(deliveryId, status);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/deliveries/{deliveryId}/location")
    @Operation(summary = "Update delivery location", description = "Update delivery current location")
    @ApiResponse(responseCode = "200", description = "Location updated")
    @ApiResponse(responseCode = "404", description = "Delivery not found")
    public ResponseEntity<Delivery> updateDeliveryLocation(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Valid @RequestBody LocationUpdateRequest request) {
        try {
            Delivery delivery = deliveryService.updateDeliveryLocation(deliveryId, request);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/deliveries/{deliveryId}/cancel")
    @Operation(summary = "Cancel delivery", description = "Cancel delivery with reason")
    @ApiResponse(responseCode = "200", description = "Delivery cancelled")
    @ApiResponse(responseCode = "404", description = "Delivery not found")
    @ApiResponse(responseCode = "400", description = "Cannot cancel delivery")
    public ResponseEntity<Delivery> cancelDelivery(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Parameter(description = "Cancellation reason") @RequestParam String reason) {
        try {
            Delivery delivery = deliveryService.cancelDelivery(deliveryId, reason);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/deliveries/{deliveryId}/rate")
    @Operation(summary = "Rate delivery", description = "Rate completed delivery")
    @ApiResponse(responseCode = "200", description = "Delivery rated")
    @ApiResponse(responseCode = "404", description = "Delivery not found")
    @ApiResponse(responseCode = "400", description = "Cannot rate delivery")
    public ResponseEntity<Delivery> rateDelivery(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Parameter(description = "Rating (1-5)") @RequestParam Double rating,
            @Parameter(description = "Feedback") @RequestParam(required = false) String feedback) {
        try {
            Delivery delivery = deliveryService.rateDelivery(deliveryId, rating, feedback);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/deliveries/partner/{partnerId}")
    @Operation(summary = "Get partner deliveries", description = "Get all deliveries for a partner")
    @ApiResponse(responseCode = "200", description = "Deliveries retrieved")
    public ResponseEntity<List<Delivery>> getPartnerDeliveries(
            @Parameter(description = "Partner ID") @PathVariable String partnerId) {
        List<Delivery> deliveries = deliveryService.getDeliveriesByPartner(partnerId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/deliveries/partner/{partnerId}/active")
    @Operation(summary = "Get active partner deliveries", description = "Get active deliveries for a partner")
    @ApiResponse(responseCode = "200", description = "Active deliveries retrieved")
    public ResponseEntity<List<Delivery>> getActivePartnerDeliveries(
            @Parameter(description = "Partner ID") @PathVariable String partnerId) {
        List<Delivery> deliveries = deliveryService.getActiveDeliveriesByPartner(partnerId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/deliveries/customer/{customerId}")
    @Operation(summary = "Get customer deliveries", description = "Get all deliveries for a customer")
    @ApiResponse(responseCode = "200", description = "Deliveries retrieved")
    public ResponseEntity<List<Delivery>> getCustomerDeliveries(
            @Parameter(description = "Customer ID") @PathVariable String customerId) {
        List<Delivery> deliveries = deliveryService.getDeliveriesByCustomer(customerId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/deliveries/restaurant/{restaurantId}")
    @Operation(summary = "Get restaurant deliveries", description = "Get all deliveries for a restaurant")
    @ApiResponse(responseCode = "200", description = "Deliveries retrieved")
    public ResponseEntity<List<Delivery>> getRestaurantDeliveries(
            @Parameter(description = "Restaurant ID") @PathVariable String restaurantId) {
        List<Delivery> deliveries = deliveryService.getDeliveriesByRestaurant(restaurantId);
        return ResponseEntity.ok(deliveries);
    }

    // Route Optimization APIs

    @PostMapping("/route/optimize/{partnerId}")
    @Operation(summary = "Optimize delivery route", description = "Optimize delivery route for a partner")
    @ApiResponse(responseCode = "200", description = "Route optimized")
    @ApiResponse(responseCode = "404", description = "Partner not found")
    @ApiResponse(responseCode = "400", description = "Route optimization disabled or no deliveries")
    public ResponseEntity<Map<String, Object>> optimizeRoute(
            @Parameter(description = "Partner ID") @PathVariable String partnerId) {
        try {
            Map<String, Object> optimizedRoute = deliveryService.optimizeRouteForPartner(partnerId);
            return ResponseEntity.ok(optimizedRoute);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Statistics APIs

    @GetMapping("/statistics")
    @Operation(summary = "Get delivery statistics", description = "Get overall delivery statistics")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved")
    public ResponseEntity<Map<String, Object>> getDeliveryStatistics() {
        Map<String, Object> stats = deliveryService.getDeliveryStatistics();
        return ResponseEntity.ok(stats);
    }

    // Health Check

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check delivery service health")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "delivery-service",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(health);
    }
}