package com.mergeeats.deliveryservice.controller;

import com.mergeeats.common.models.Delivery;
import com.mergeeats.common.models.Address;
import com.mergeeats.common.enums.DeliveryStatus;
import com.mergeeats.deliveryservice.service.DeliveryService;
import com.mergeeats.deliveryservice.service.DeliveryPartnerService;
import com.mergeeats.deliveryservice.dto.CreateDeliveryRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/deliveries")
@Tag(name = "Delivery Management", description = "APIs for managing deliveries and delivery operations")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private DeliveryPartnerService deliveryPartnerService;

    @Operation(summary = "Create a new delivery", description = "Creates a new delivery for an order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Delivery created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<Delivery> createDelivery(@Valid @RequestBody CreateDeliveryRequest request) {
        try {
            Delivery delivery = deliveryService.createDelivery(request);
            return new ResponseEntity<>(delivery, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get delivery by ID", description = "Retrieves a delivery by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery found"),
        @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    @GetMapping("/{deliveryId}")
    public ResponseEntity<Delivery> getDeliveryById(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        Optional<Delivery> delivery = deliveryService.getDeliveryById(deliveryId);
        return delivery.map(d -> ResponseEntity.ok(d))
                      .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get delivery by order ID", description = "Retrieves a delivery by order ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery found"),
        @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Delivery> getDeliveryByOrderId(
            @Parameter(description = "Order ID") @PathVariable String orderId) {
        Optional<Delivery> delivery = deliveryService.getDeliveryByOrderId(orderId);
        return delivery.map(d -> ResponseEntity.ok(d))
                      .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Assign delivery partner", description = "Assigns a delivery partner to a delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner assigned successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "400", description = "No available partner found")
    })
    @PostMapping("/{deliveryId}/assign")
    public ResponseEntity<Delivery> assignDeliveryPartner(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        try {
            Delivery delivery = deliveryService.assignDeliveryPartner(deliveryId);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update delivery status", description = "Updates the status of a delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition")
    })
    @PutMapping("/{deliveryId}/status")
    public ResponseEntity<Delivery> updateDeliveryStatus(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Parameter(description = "New delivery status") @RequestParam DeliveryStatus status) {
        try {
            Delivery delivery = deliveryService.updateDeliveryStatus(deliveryId, status);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update delivery location", description = "Updates the current location of a delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Location updated successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    @PutMapping("/{deliveryId}/location")
    public ResponseEntity<Delivery> updateDeliveryLocation(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Valid @RequestBody Address currentLocation) {
        try {
            Delivery delivery = deliveryService.updateDeliveryLocation(deliveryId, currentLocation);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Cancel delivery", description = "Cancels a delivery with a reason")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "400", description = "Cannot cancel completed delivery")
    })
    @PutMapping("/{deliveryId}/cancel")
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

    @Operation(summary = "Get deliveries by partner", description = "Retrieves all deliveries for a delivery partner")
    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<List<Delivery>> getDeliveriesByPartnerId(
            @Parameter(description = "Partner ID") @PathVariable String partnerId) {
        List<Delivery> deliveries = deliveryService.getDeliveriesByPartnerId(partnerId);
        return ResponseEntity.ok(deliveries);
    }

    @Operation(summary = "Get active deliveries by partner", description = "Retrieves active deliveries for a delivery partner")
    @GetMapping("/partner/{partnerId}/active")
    public ResponseEntity<List<Delivery>> getActiveDeliveriesByPartnerId(
            @Parameter(description = "Partner ID") @PathVariable String partnerId) {
        List<Delivery> deliveries = deliveryService.getActiveDeliveriesByPartnerId(partnerId);
        return ResponseEntity.ok(deliveries);
    }

    @Operation(summary = "Get deliveries by customer", description = "Retrieves all deliveries for a customer")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Delivery>> getDeliveriesByCustomerId(
            @Parameter(description = "Customer ID") @PathVariable String customerId) {
        List<Delivery> deliveries = deliveryService.getDeliveriesByCustomerId(customerId);
        return ResponseEntity.ok(deliveries);
    }

    @Operation(summary = "Get deliveries by restaurant", description = "Retrieves all deliveries for a restaurant")
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Delivery>> getDeliveriesByRestaurantId(
            @Parameter(description = "Restaurant ID") @PathVariable String restaurantId) {
        List<Delivery> deliveries = deliveryService.getDeliveriesByRestaurantId(restaurantId);
        return ResponseEntity.ok(deliveries);
    }

    @Operation(summary = "Get deliveries by status", description = "Retrieves deliveries by their status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Delivery>> getDeliveriesByStatus(
            @Parameter(description = "Delivery status") @PathVariable DeliveryStatus status) {
        List<Delivery> deliveries = deliveryService.getDeliveriesByStatus(status);
        return ResponseEntity.ok(deliveries);
    }

    @Operation(summary = "Get delivery statistics", description = "Retrieves delivery statistics and metrics")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getDeliveryStatistics() {
        try {
            Map<String, Object> statistics = deliveryPartnerService.getDeliveryStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}