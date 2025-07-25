package com.mergeeats.deliveryservice.repository;

import com.mergeeats.common.models.Delivery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends MongoRepository<Delivery, String> {

    // Find by order ID
    Optional<Delivery> findByOrderId(String orderId);

    // Find by delivery partner
    List<Delivery> findByDeliveryPartnerId(String deliveryPartnerId);

    // Find by customer
    List<Delivery> findByCustomerId(String customerId);

    // Find by restaurant
    List<Delivery> findByRestaurantId(String restaurantId);

    // Find by status
    List<Delivery> findByStatus(String status);

    // Find by delivery partner and status
    List<Delivery> findByDeliveryPartnerIdAndStatus(String deliveryPartnerId, String status);

    // Find active deliveries for partner
    @Query("{'deliveryPartnerId': ?0, 'status': {'$in': ['ASSIGNED', 'PICKED_UP', 'IN_TRANSIT']}}")
    List<Delivery> findActiveDeliveriesByPartnerId(String deliveryPartnerId);

    // Find deliveries in time range
    List<Delivery> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find deliveries by partner in time range
    List<Delivery> findByDeliveryPartnerIdAndCreatedAtBetween(String deliveryPartnerId, LocalDateTime startDate, LocalDateTime endDate);

    // Find deliveries by customer in time range
    List<Delivery> findByCustomerIdAndCreatedAtBetween(String customerId, LocalDateTime startDate, LocalDateTime endDate);

    // Find deliveries by restaurant in time range
    List<Delivery> findByRestaurantIdAndCreatedAtBetween(String restaurantId, LocalDateTime startDate, LocalDateTime endDate);

    // Find by tracking code
    Optional<Delivery> findByTrackingCode(String trackingCode);

    // Find merged deliveries
    @Query("{'mergedOrderIds': {'$exists': true, '$ne': null, '$not': {'$size': 0}}}")
    List<Delivery> findMergedDeliveries();

    // Find deliveries with customer rating
    @Query("{'customerRating': {'$exists': true, '$ne': null}}")
    List<Delivery> findDeliveriesWithRating();

    // Find deliveries by rating range
    @Query("{'customerRating': {'$gte': ?0, '$lte': ?1}}")
    List<Delivery> findByCustomerRatingBetween(Double minRating, Double maxRating);

    // Count deliveries by status
    long countByStatus(String status);

    // Count deliveries by partner
    long countByDeliveryPartnerId(String deliveryPartnerId);

    // Count completed deliveries by partner
    long countByDeliveryPartnerIdAndStatus(String deliveryPartnerId, String status);

    // Find pending assignments
    @Query("{'status': 'ASSIGNED', 'assignedAt': {'$lt': ?0}}")
    List<Delivery> findPendingAssignments(LocalDateTime cutoffTime);

    // Find deliveries for route optimization
    @Query("{'deliveryPartnerId': ?0, 'status': {'$in': ['ASSIGNED', 'PICKED_UP']}, " +
           "'pickupAddress.latitude': {'$exists': true}, 'deliveryAddress.latitude': {'$exists': true}}")
    List<Delivery> findDeliveriesForRouteOptimization(String deliveryPartnerId);
}