package com.mergeeats.deliveryservice.repository;

import com.mergeeats.common.models.Delivery;
import com.mergeeats.common.enums.DeliveryStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends MongoRepository<Delivery, String> {

    // Find by order
    Optional<Delivery> findByOrderId(String orderId);
    List<Delivery> findByOrderIdIn(List<String> orderIds);

    // Find by delivery partner
    List<Delivery> findByDeliveryPartnerId(String deliveryPartnerId);
    List<Delivery> findByDeliveryPartnerIdAndStatus(String deliveryPartnerId, DeliveryStatus status);
    
    @Query("{'deliveryPartnerId': ?0, 'status': {'$in': ?1}}")
    List<Delivery> findByDeliveryPartnerIdAndStatusIn(String deliveryPartnerId, List<DeliveryStatus> statuses);

    // Find by customer
    List<Delivery> findByCustomerId(String customerId);
    List<Delivery> findByCustomerIdAndStatus(String customerId, DeliveryStatus status);

    // Find by restaurant
    List<Delivery> findByRestaurantId(String restaurantId);
    List<Delivery> findByRestaurantIdAndStatus(String restaurantId, DeliveryStatus status);

    // Find by status
    List<Delivery> findByStatus(DeliveryStatus status);
    List<Delivery> findByStatusIn(List<DeliveryStatus> statuses);

    // Find pending deliveries for assignment
    @Query("{'status': 'PENDING', 'createdAt': {'$gte': ?0}}")
    List<Delivery> findPendingDeliveriesAfter(LocalDateTime after);

    // Find active deliveries for a partner
    @Query("{'deliveryPartnerId': ?0, 'status': {'$in': ['ASSIGNED', 'ACCEPTED', 'PICKED_UP', 'IN_TRANSIT']}}")
    List<Delivery> findActiveDeliveriesByPartnerId(String deliveryPartnerId);

    // Find deliveries in area for optimization
    @Query("{'pickupAddress.latitude': {'$gte': ?0, '$lte': ?1}, " +
           "'pickupAddress.longitude': {'$gte': ?2, '$lte': ?3}, " +
           "'status': {'$in': ['PENDING', 'ASSIGNED']}}")
    List<Delivery> findDeliveriesInArea(Double minLat, Double maxLat, Double minLng, Double maxLng);

    // Find merged deliveries
    List<Delivery> findByIsMergedDelivery(Boolean isMerged);
    List<Delivery> findByMergedOrderIdsContaining(String orderId);

    // Time-based queries
    List<Delivery> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Delivery> findByScheduledPickupTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Delivery> findByEstimatedDeliveryTimeBetween(LocalDateTime start, LocalDateTime end);

    // Statistics queries
    @Query("{'deliveryPartnerId': ?0, 'status': 'DELIVERED', 'deliveredAt': {'$gte': ?1, '$lte': ?2}}")
    List<Delivery> findCompletedDeliveriesByPartnerInPeriod(String partnerId, LocalDateTime start, LocalDateTime end);

    @Query("{'restaurantId': ?0, 'createdAt': {'$gte': ?1, '$lte': ?2}}")
    List<Delivery> findDeliveriesByRestaurantInPeriod(String restaurantId, LocalDateTime start, LocalDateTime end);

    // Count queries
    long countByDeliveryPartnerIdAndStatus(String deliveryPartnerId, DeliveryStatus status);
    long countByStatusAndCreatedAtBetween(DeliveryStatus status, LocalDateTime start, LocalDateTime end);

    // Distance-based queries (for route optimization)
    @Query("{'status': {'$in': ['PENDING', 'ASSIGNED']}, " +
           "'pickupAddress.latitude': {'$exists': true}, " +
           "'pickupAddress.longitude': {'$exists': true}}")
    List<Delivery> findDeliveriesWithLocationForOptimization();

    // Find overdue deliveries
    @Query("{'status': {'$in': ['ASSIGNED', 'ACCEPTED', 'PICKED_UP', 'IN_TRANSIT']}, " +
           "'estimatedDeliveryTime': {'$lt': ?0}}")
    List<Delivery> findOverdueDeliveries(LocalDateTime currentTime);

    // Find deliveries requiring partner assignment
    @Query("{'status': 'PENDING', 'deliveryPartnerId': null}")
    List<Delivery> findDeliveriesRequiringAssignment();
}