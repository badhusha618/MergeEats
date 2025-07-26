package com.mergeeats.orderservice.controller;

import com.mergeeats.common.models.Order;
import com.mergeeats.common.enums.OrderStatus;
import com.mergeeats.orderservice.dto.CreateOrderRequest;
import com.mergeeats.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@Tag(name = "Order Management", 
     description = "**Comprehensive Order Management APIs**\n\n" +
                  "This controller provides all order-related operations including:\n" +
                  "- Order creation and management\n" +
                  "- AI-powered order merging\n" +
                  "- Order status tracking\n" +
                  "- Group order functionality\n" +
                  "- Order statistics and analytics")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping
    @Operation(
        summary = "Create a new order",
        description = "**Creates a new order and triggers AI-powered merging if enabled**\n\n" +
                     "This endpoint handles order creation with advanced features:\n" +
                     "- Multi-restaurant order support\n" +
                     "- AI-powered order merging for efficiency\n" +
                     "- Real-time order tracking\n" +
                     "- Payment integration\n" +
                     "- Delivery coordination",
        tags = {"Order Creation"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Order created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Order.class),
                examples = @ExampleObject(
                    name = "Successful Order Creation",
                    value = """
                        {
                          "id": "order_123456789",
                          "userId": "user_987654321",
                          "restaurantId": "restaurant_123456789",
                          "items": [
                            {
                              "menuItemId": "item_123",
                              "name": "Margherita Pizza",
                              "quantity": 2,
                              "price": 12.99,
                              "specialInstructions": "Extra cheese please"
                            }
                          ],
                          "status": "PENDING",
                          "totalAmount": 25.98,
                          "deliveryFee": 2.99,
                          "tax": 2.60,
                          "grandTotal": 31.57,
                          "deliveryAddress": "123 Main St, City, State 12345",
                          "estimatedDeliveryTime": "2024-01-15T12:30:00Z",
                          "createdAt": "2024-01-15T11:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid order data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                        {
                          "message": "Validation failed",
                          "errors": [
                            "User ID is required",
                            "Restaurant ID is required",
                            "Order items are required"
                          ]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Order> createOrder(
        @Parameter(
            description = "Order creation data",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Order Creation",
                    value = """
                        {
                          "userId": "user_987654321",
                          "restaurantId": "restaurant_123456789",
                          "deliveryAddress": "123 Main St, City, State 12345",
                          "specialInstructions": "Please deliver to the front door",
                          "paymentMethod": "CREDIT_CARD",
                          "items": [
                            {
                              "menuItemId": "item_123",
                              "quantity": 2,
                              "specialInstructions": "Extra cheese please"
                            }
                          ]
                        }
                        """
                )
            )
        )
        @Valid @RequestBody CreateOrderRequest request) {
        try {
            Order order = orderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{orderId}")
    @Operation(
        summary = "Get order by ID",
        description = "**Retrieves detailed order information by ID**\n\n" +
                     "Returns comprehensive order data including:\n" +
                     "- Order items and quantities\n" +
                     "- Current status and tracking\n" +
                     "- Payment and delivery information\n" +
                     "- Timestamps and history",
        tags = {"Order Information"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Order.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "Order not found",
                          "errors": ["Order with ID order_123456789 does not exist"]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Order> getOrderById(
        @Parameter(example = "order_123456789", description = "Unique order identifier")
        @PathVariable String orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user", description = "Retrieves all orders for a specific user")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable String userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get orders by restaurant", description = "Retrieves all orders for a specific restaurant")
    public ResponseEntity<List<Order>> getOrdersByRestaurantId(@PathVariable String restaurantId) {
        List<Order> orders = orderService.getOrdersByRestaurantId(restaurantId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status", description = "Retrieves all orders with a specific status")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an order")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable String orderId,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            String statusStr = statusUpdate.get("status");
            OrderStatus newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
            
            Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancels an order with an optional reason")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable String orderId,
            @RequestBody(required = false) Map<String, String> cancellationData) {
        try {
            String reason = cancellationData != null ? cancellationData.get("reason") : "User requested cancellation";
            Order cancelledOrder = orderService.cancelOrder(orderId, reason);
            return ResponseEntity.ok(cancelledOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/group")
    @Operation(summary = "Create group order", description = "Creates a group order with multiple individual orders")
    public ResponseEntity<List<Order>> createGroupOrder(
            @RequestBody Map<String, Object> groupOrderData) {
        try {
            @SuppressWarnings("unchecked")
            List<CreateOrderRequest> orderRequests = (List<CreateOrderRequest>) groupOrderData.get("orders");
            String creatorId = (String) groupOrderData.get("creatorId");
            
            List<Order> groupOrders = orderService.createGroupOrder(orderRequests, creatorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(groupOrders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/group/{groupOrderId}")
    @Operation(summary = "Get group orders", description = "Retrieves all orders in a group order")
    public ResponseEntity<List<Order>> getGroupOrders(@PathVariable String groupOrderId) {
        List<Order> groupOrders = orderService.getGroupOrders(groupOrderId);
        return ResponseEntity.ok(groupOrders);
    }
    
    @GetMapping("/merged/{mergedOrderId}")
    @Operation(summary = "Get merged orders", description = "Retrieves all orders that were merged together")
    public ResponseEntity<List<Order>> getMergedOrders(@PathVariable String mergedOrderId) {
        List<Order> mergedOrders = orderService.getMergedOrders(mergedOrderId);
        return ResponseEntity.ok(mergedOrders);
    }
    
    @GetMapping("/restaurant/{restaurantId}/active")
    @Operation(summary = "Get active restaurant orders", description = "Retrieves active orders for a restaurant")
    public ResponseEntity<List<Order>> getActiveOrdersByRestaurant(@PathVariable String restaurantId) {
        List<Order> activeOrders = orderService.getActiveOrdersByRestaurant(restaurantId);
        return ResponseEntity.ok(activeOrders);
    }
    
    @GetMapping("/recent")
    @Operation(summary = "Get recent orders", description = "Retrieves recent orders within specified hours")
    public ResponseEntity<List<Order>> getRecentOrders(
            @RequestParam(defaultValue = "24") int hours) {
        List<Order> recentOrders = orderService.getRecentOrders(hours);
        return ResponseEntity.ok(recentOrders);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "Get order statistics", description = "Retrieves order statistics for a date range")
    public ResponseEntity<Map<String, Object>> getOrderStatistics(
            @RequestParam(required = false) String restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Map<String, Object> statistics = orderService.getOrderStatistics(restaurantId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Service health check endpoint")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Order Service is running");
    }
    
    // Additional endpoints for order management
    
    @GetMapping("/user/{userId}/status/{status}")
    @Operation(summary = "Get user orders by status", description = "Retrieves orders for a user with specific status")
    public ResponseEntity<List<Order>> getUserOrdersByStatus(
            @PathVariable String userId,
            @PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByUserId(userId)
                .stream()
                .filter(order -> order.getStatus() == status)
                .toList();
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/restaurant/{restaurantId}/status/{status}")
    @Operation(summary = "Get restaurant orders by status", description = "Retrieves orders for a restaurant with specific status")
    public ResponseEntity<List<Order>> getRestaurantOrdersByStatus(
            @PathVariable String restaurantId,
            @PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByRestaurantId(restaurantId)
                .stream()
                .filter(order -> order.getStatus() == status)
                .toList();
        return ResponseEntity.ok(orders);
    }
    
    @PostMapping("/{orderId}/estimate-delivery")
    @Operation(summary = "Update delivery estimate", description = "Updates the estimated delivery time for an order")
    public ResponseEntity<Order> updateDeliveryEstimate(
            @PathVariable String orderId,
            @RequestBody Map<String, String> estimateData) {
        try {
            Order order = orderService.getOrderById(orderId);
            LocalDateTime newEstimate = LocalDateTime.parse(estimateData.get("estimatedDeliveryTime"));
            order.setEstimatedDeliveryTime(newEstimate);
            
            // In a real implementation, this would be saved through the service
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}