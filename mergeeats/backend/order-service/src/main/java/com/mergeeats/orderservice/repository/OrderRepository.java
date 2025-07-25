package com.mergeeats.orderservice.repository;

import com.mergeeats.common.models.Order;
import com.mergeeats.common.enums.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    List<Order> findByUserId(String userId);
    
    List<Order> findByRestaurantId(String restaurantId);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByUserIdAndStatus(String userId, OrderStatus status);
    
    List<Order> findByRestaurantIdAndStatus(String restaurantId, OrderStatus status);
    
    @Query("{'orderTime': {$gte: ?0, $lte: ?1}}")
    List<Order> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("{'deliveryAddress.latitude': {$gte: ?0, $lte: ?1}, 'deliveryAddress.longitude': {$gte: ?2, $lte: ?3}, 'status': ?4}")
    List<Order> findOrdersInAreaWithStatus(double minLat, double maxLat, double minLng, double maxLng, OrderStatus status);
    
    List<Order> findByGroupOrderId(String groupOrderId);
    
    List<Order> findByIsGroupOrderTrue();
    
    List<Order> findByIsMergedTrue();
    
    List<Order> findByMergedOrderId(String mergedOrderId);
    
    @Query("{'status': {$in: ['PENDING', 'CONFIRMED']}, 'orderTime': {$gte: ?0}}")
    List<Order> findOrdersEligibleForMerging(LocalDateTime timeThreshold);
    
    @Query("{'restaurantId': ?0, 'status': {$in: ['PENDING', 'CONFIRMED', 'PREPARING']}, 'orderTime': {$gte: ?1}}")
    List<Order> findActiveOrdersByRestaurant(String restaurantId, LocalDateTime timeThreshold);
    
    Optional<Order> findByDeliveryTrackingId(String trackingId);
    
    List<Order> findByDeliveryPartnerId(String deliveryPartnerId);
    
    @Query("{'createdAt': {$gte: ?0}, 'status': {$ne: 'CANCELLED'}}")
    List<Order> findRecentOrders(LocalDateTime since);
    
    long countByRestaurantIdAndCreatedAtBetween(String restaurantId, LocalDateTime start, LocalDateTime end);
    
    long countByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime start, LocalDateTime end);
}