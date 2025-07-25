package com.mergeeats.deliveryservice.controller;

import com.mergeeats.common.models.Delivery;
import com.mergeeats.common.enums.DeliveryStatus;
import com.mergeeats.deliveryservice.service.DeliveryService;
import com.mergeeats.deliveryservice.dto.CreateDeliveryRequest;
import com.mergeeats.deliveryservice.dto.UpdateLocationRequest;
import com.mergeeats.deliveryservice.dto.AssignDeliveryRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/deliveries")
@Tag(name = "Delivery Management", description = "APIs for managing deliveries, tracking, and assignments")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping
    @Operation(summary = "Create new delivery", description = "Creates a new delivery for an order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Delivery created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Delivery> createDelivery(
            @Valid @RequestBody CreateDeliveryRequest request) {
        try {
            Delivery delivery = deliveryService.createDelivery(request);
            return new ResponseEntity<>(delivery, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{deliveryId}")
    @Operation(summary = "Get delivery by ID", description = "Retrieves a delivery by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery found"),
        @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    public ResponseEntity<Delivery> getDeliveryById(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        Optional<Delivery> delivery = deliveryService.getDeliveryById(deliveryId);
        return delivery.map(d -> ResponseEntity.ok(d))
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get delivery by order ID", description = "Retrieves a delivery by order ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery found"),
        @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    public ResponseEntity<Delivery> getDeliveryByOrderId(
            @Parameter(description = "Order ID") @PathVariable String orderId) {
        Optional<Delivery> delivery = deliveryService.getDeliveryByOrderId(orderId);
        return delivery.map(d -> ResponseEntity.ok(d))
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/partner/{partnerId}")
    @Operation(summary = "Get deliveries by partner", description = "Retrieves all deliveries for a delivery partner")
    @ApiResponse(responseCode = "200", description = "Deliveries retrieved successfully")
    public ResponseEntity<List<Delivery>> getDeliveriesByPartnerId(
            @Parameter(description = "Delivery partner ID") @PathVariable String partnerId) {
        List<Delivery> deliveries = deliveryService.getDeliveriesByPartnerId(partnerId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/partner/{partnerId}/active")
    @Operation(summary = "Get active deliveries for partner", description = "Retrieves active deliveries for a delivery partner")
    @ApiResponse(responseCode = "200", description = "Active deliveries retrieved successfully")
    public ResponseEntity<List<Delivery>> getActiveDeliveriesForPartner(
            @Parameter(description = "Delivery partner ID") @PathVariable String partnerId) {
        List<Delivery> deliveries = deliveryService.getActiveDeliveriesForPartner(partnerId);
        return ResponseEntity.ok(deliveries);
    }

    @PostMapping("/{deliveryId}/assign")
    @Operation(summary = "Assign delivery to partner", description = "Assigns a delivery to a delivery partner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery assigned successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid assignment request"),
        @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    public ResponseEntity<Delivery> assignDelivery(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Valid @RequestBody AssignDeliveryRequest request) {
        try {
            Delivery delivery = deliveryService.assignDelivery(deliveryId, request);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{deliveryId}/auto-assign")
    @Operation(summary = "Auto-assign delivery", description = "Automatically assigns a delivery to the best available partner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery auto-assigned successfully"),
        @ApiResponse(responseCode = "400", description = "Auto-assignment failed"),
        @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    public ResponseEntity<Map<String, Object>> autoAssignDelivery(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        boolean assigned = deliveryService.autoAssignDelivery(deliveryId);
        Map<String, Object> response = Map.of(
            "deliveryId", deliveryId,
            "assigned", assigned,
            "message", assigned ? "Delivery auto-assigned successfully" : "No available partners found"
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{deliveryId}/status")
    @Operation(summary = "Update delivery status", description = "Updates the status of a delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    public ResponseEntity<Delivery> updateDeliveryStatus(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Parameter(description = "New delivery status") @RequestParam DeliveryStatus status,
            @Parameter(description = "Status update message") @RequestParam(required = false, defaultValue = "") String message) {
        try {
            Delivery delivery = deliveryService.updateDeliveryStatus(deliveryId, status, message);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{deliveryId}/location")
    @Operation(summary = "Update delivery location", description = "Updates the current location of a delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Location updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid location data"),
        @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    public ResponseEntity<Map<String, String>> updateDeliveryLocation(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Valid @RequestBody UpdateLocationRequest request) {
        try {
            deliveryService.updateDeliveryLocation(deliveryId, request);
            Map<String, String> response = Map.of(
                "deliveryId", deliveryId,
                "message", "Location updated successfully"
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{deliveryId}/location")
    @Operation(summary = "Get delivery location", description = "Gets the current real-time location of a delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Location retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Location not found")
    })
    public ResponseEntity<Map<String, Object>> getDeliveryLocation(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        Map<String, Object> location = deliveryService.getDeliveryLocation(deliveryId);
        if (location != null) {
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{deliveryId}/cancel")
    @Operation(summary = "Cancel delivery", description = "Cancels a delivery with a reason")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot cancel delivery"),
        @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    public ResponseEntity<Delivery> cancelDelivery(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Parameter(description = "Cancellation reason") @RequestParam String reason) {
        try {
            Delivery delivery = deliveryService.cancelDelivery(deliveryId, reason);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get deliveries by status", description = "Retrieves all deliveries with a specific status")
    @ApiResponse(responseCode = "200", description = "Deliveries retrieved successfully")
    public ResponseEntity<List<Delivery>> getDeliveriesByStatus(
            @Parameter(description = "Delivery status") @PathVariable DeliveryStatus status) {
        List<Delivery> deliveries = deliveryService.getDeliveriesByStatus(status);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue deliveries", description = "Retrieves all deliveries that are overdue")
    @ApiResponse(responseCode = "200", description = "Overdue deliveries retrieved successfully")
    public ResponseEntity<List<Delivery>> getOverdueDeliveries() {
        List<Delivery> deliveries = deliveryService.getOverdueDeliveries();
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/partner/{partnerId}/statistics")
    @Operation(summary = "Get partner statistics", description = "Gets delivery statistics for a partner within a date range")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getPartnerStatistics(
            @Parameter(description = "Delivery partner ID") @PathVariable String partnerId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Map<String, Object> statistics = deliveryService.getPartnerStatistics(partnerId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @PostMapping("/batch")
    @Operation(summary = "Create batch delivery", description = "Creates a batch delivery assignment for multiple orders")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch delivery created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid batch request"),
        @ApiResponse(responseCode = "404", description = "Some orders not found")
    })
    public ResponseEntity<List<Delivery>> createBatchDelivery(
            @Parameter(description = "List of order IDs") @RequestParam List<String> orderIds,
            @Parameter(description = "Delivery partner ID") @RequestParam String partnerId) {
        try {
            List<Delivery> deliveries = deliveryService.createBatchDelivery(orderIds, partnerId);
            return ResponseEntity.ok(deliveries);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Service health check endpoint")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = Map.of(
            "status", "UP",
            "service", "delivery-service",
            "timestamp", LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(health);
    }
}