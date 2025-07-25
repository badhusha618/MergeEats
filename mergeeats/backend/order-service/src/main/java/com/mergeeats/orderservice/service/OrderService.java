package com.mergeeats.orderservice.service;

import com.mergeeats.common.models.Order;
import com.mergeeats.common.models.OrderItem;
import com.mergeeats.common.enums.OrderStatus;
import com.mergeeats.common.enums.PaymentStatus;
import com.mergeeats.orderservice.dto.CreateOrderRequest;
import com.mergeeats.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Autowired
    private OrderMergingService orderMergingService;
    
    @Value("${order.merge.enabled:true}")
    private boolean mergingEnabled;
    
    @Value("${order.merge.time-window-minutes:15}")
    private int mergeTimeWindowMinutes;
    
    public Order createOrder(CreateOrderRequest request) {
        // Validate order items and calculate total
        BigDecimal totalAmount = calculateOrderTotal(request.getItems());
        
        // Create order
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setRestaurantId(request.getRestaurantId());
        order.setItems(request.getItems());
        order.setTotalAmount(totalAmount);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setSpecialInstructions(request.getSpecialInstructions());
        order.setGroupOrder(request.isGroupOrder());
        order.setGroupOrderId(request.getGroupOrderId());
        
        // Calculate fees
        calculateOrderFees(order);
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Publish order created event
        publishOrderEvent("ORDER_CREATED", savedOrder);
        
        // Trigger order merging if enabled
        if (mergingEnabled && !request.isGroupOrder()) {
            triggerOrderMerging(savedOrder);
        }
        
        return savedOrder;
    }
    
    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }
    
    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
    
    public List<Order> getOrdersByRestaurantId(String restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }
    
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    public Order updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        OrderStatus previousStatus = order.getStatus();
        
        // Validate status transition
        if (!isValidStatusTransition(previousStatus, newStatus)) {
            throw new RuntimeException("Invalid status transition from " + previousStatus + " to " + newStatus);
        }
        
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        // Set delivery time if order is delivered
        if (newStatus == OrderStatus.DELIVERED) {
            order.setActualDeliveryTime(LocalDateTime.now());
        }
        
        Order updatedOrder = orderRepository.save(order);
        
        // Publish status update event
        publishOrderEvent("ORDER_STATUS_UPDATED", updatedOrder);
        
        return updatedOrder;
    }
    
    public Order cancelOrder(String orderId, String reason) {
        Order order = getOrderById(orderId);
        
        if (!order.getStatus().canBeCancelled()) {
            throw new RuntimeException("Order cannot be cancelled in current status: " + order.getStatus());
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        
        Order cancelledOrder = orderRepository.save(order);
        
        // Publish cancellation event
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", orderId);
        eventData.put("reason", reason);
        eventData.put("order", cancelledOrder);
        
        kafkaTemplate.send("order-events", orderId, eventData);
        
        return cancelledOrder;
    }
    
    public List<Order> createGroupOrder(List<CreateOrderRequest> orderRequests, String groupOrderCreatorId) {
        String groupOrderId = UUID.randomUUID().toString();
        List<Order> groupOrders = new ArrayList<>();
        
        for (CreateOrderRequest request : orderRequests) {
            request.setGroupOrder(true);
            request.setGroupOrderId(groupOrderId);
            
            Order order = createOrder(request);
            order.setGroupOrderCreatorId(groupOrderCreatorId);
            
            groupOrders.add(orderRepository.save(order));
        }
        
        // Publish group order created event
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("groupOrderId", groupOrderId);
        eventData.put("creatorId", groupOrderCreatorId);
        eventData.put("orders", groupOrders);
        
        kafkaTemplate.send("order-events", groupOrderId, eventData);
        
        return groupOrders;
    }
    
    public List<Order> getGroupOrders(String groupOrderId) {
        return orderRepository.findByGroupOrderId(groupOrderId);
    }
    
    @Async
    public void triggerOrderMerging(Order newOrder) {
        try {
            LocalDateTime timeThreshold = LocalDateTime.now().minusMinutes(mergeTimeWindowMinutes);
            List<Order> eligibleOrders = orderRepository.findOrdersEligibleForMerging(timeThreshold);
            
            // Filter orders by restaurant and location proximity
            List<Order> candidateOrders = eligibleOrders.stream()
                    .filter(order -> order.getRestaurantId().equals(newOrder.getRestaurantId()))
                    .filter(order -> !order.getOrderId().equals(newOrder.getOrderId()))
                    .filter(order -> orderMergingService.isWithinDeliveryRadius(newOrder, order))
                    .collect(Collectors.toList());
            
            if (!candidateOrders.isEmpty()) {
                candidateOrders.add(newOrder);
                orderMergingService.mergeOrders(candidateOrders);
            }
        } catch (Exception e) {
            // Log error but don't fail the order creation
            System.err.println("Error in order merging: " + e.getMessage());
        }
    }
    
    public List<Order> getMergedOrders(String mergedOrderId) {
        return orderRepository.findByMergedOrderId(mergedOrderId);
    }
    
    public List<Order> getActiveOrdersByRestaurant(String restaurantId) {
        LocalDateTime timeThreshold = LocalDateTime.now().minusHours(24);
        return orderRepository.findActiveOrdersByRestaurant(restaurantId, timeThreshold);
    }
    
    public List<Order> getRecentOrders(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return orderRepository.findRecentOrders(since);
    }
    
    public Map<String, Object> getOrderStatistics(String restaurantId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        if (restaurantId != null) {
            long totalOrders = orderRepository.countByRestaurantIdAndCreatedAtBetween(restaurantId, startDate, endDate);
            stats.put("totalOrders", totalOrders);
        }
        
        // Status-wise counts
        for (OrderStatus status : OrderStatus.values()) {
            long count = orderRepository.countByStatusAndCreatedAtBetween(status, startDate, endDate);
            stats.put(status.name().toLowerCase() + "Orders", count);
        }
        
        return stats;
    }
    
    private BigDecimal calculateOrderTotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private void calculateOrderFees(Order order) {
        BigDecimal subtotal = order.getTotalAmount();
        
        // Calculate delivery fee (simplified logic)
        BigDecimal deliveryFee = BigDecimal.valueOf(5.00); // Base delivery fee
        order.setDeliveryFee(deliveryFee);
        
        // Calculate service fee (2% of subtotal)
        BigDecimal serviceFee = subtotal.multiply(BigDecimal.valueOf(0.02));
        order.setServiceFee(serviceFee);
        
        // Calculate tax (8% of subtotal)
        BigDecimal taxAmount = subtotal.multiply(BigDecimal.valueOf(0.08));
        order.setTaxAmount(taxAmount);
        
        // Update total amount
        BigDecimal finalTotal = subtotal.add(deliveryFee).add(serviceFee).add(taxAmount);
        if (order.getDiscountAmount() != null) {
            finalTotal = finalTotal.subtract(order.getDiscountAmount());
        }
        order.setTotalAmount(finalTotal);
    }
    
    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid status transitions
        Map<OrderStatus, Set<OrderStatus>> validTransitions = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.REJECTED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.PREPARING, OrderStatus.CANCELLED),
            OrderStatus.PREPARING, Set.of(OrderStatus.READY_FOR_PICKUP, OrderStatus.CANCELLED),
            OrderStatus.READY_FOR_PICKUP, Set.of(OrderStatus.PICKED_UP),
            OrderStatus.PICKED_UP, Set.of(OrderStatus.OUT_FOR_DELIVERY),
            OrderStatus.OUT_FOR_DELIVERY, Set.of(OrderStatus.DELIVERED)
        );
        
        return validTransitions.getOrDefault(currentStatus, Set.of()).contains(newStatus);
    }
    
    private void publishOrderEvent(String eventType, Order order) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventType);
            event.put("orderId", order.getOrderId());
            event.put("userId", order.getUserId());
            event.put("restaurantId", order.getRestaurantId());
            event.put("status", order.getStatus());
            event.put("totalAmount", order.getTotalAmount());
            event.put("timestamp", LocalDateTime.now());
            
            kafkaTemplate.send("order-events", order.getOrderId(), event);
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to publish order event: " + e.getMessage());
        }
    }
}