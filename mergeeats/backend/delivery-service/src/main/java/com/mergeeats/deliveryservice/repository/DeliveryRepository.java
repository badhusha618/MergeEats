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

    // Find deliveries by delivery partner
    List<Delivery> findByDeliveryPartnerId(String deliveryPartnerId);

    // Find deliveries by order ID
    Optional<Delivery> findByOrderId(String orderId);

    // Find deliveries by status
    List<Delivery> findByStatus(DeliveryStatus status);

    // Find active deliveries for a delivery partner
    @Query("{'deliveryPartnerId': ?0, 'status': {'$in': ['ASSIGNED', 'PICKED_UP', 'IN_TRANSIT']}}")
    List<Delivery> findActiveDeliveriesByPartnerId(String deliveryPartnerId);

    // Find available delivery partners in area
    @Query("{'status': 'AVAILABLE', 'currentLocation': {'$near': {'$geometry': {'type': 'Point', 'coordinates': [?0, ?1]}, '$maxDistance': ?2}}}")
    List<Delivery> findAvailablePartnersInArea(double longitude, double latitude, double maxDistanceMeters);

    // Find deliveries by restaurant
    List<Delivery> findByRestaurantId(String restaurantId);

    // Find deliveries by customer
    List<Delivery> findByCustomerId(String customerId);

    // Find deliveries created within time range
    List<Delivery> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    // Find deliveries by estimated delivery time
    List<Delivery> findByEstimatedDeliveryTimeBefore(LocalDateTime time);

    // Find overdue deliveries
    @Query("{'estimatedDeliveryTime': {'$lt': ?0}, 'status': {'$in': ['ASSIGNED', 'PICKED_UP', 'IN_TRANSIT']}}")
    List<Delivery> findOverdueDeliveries(LocalDateTime currentTime);

    // Find deliveries for batch assignment
    @Query("{'status': 'PENDING', 'pickupLocation': {'$near': {'$geometry': {'type': 'Point', 'coordinates': [?0, ?1]}, '$maxDistance': ?2}}}")
    List<Delivery> findPendingDeliveriesInArea(double longitude, double latitude, double maxDistanceMeters);

    // Count active deliveries for partner
    @Query(value = "{'deliveryPartnerId': ?0, 'status': {'$in': ['ASSIGNED', 'PICKED_UP', 'IN_TRANSIT']}}", count = true)
    long countActiveDeliveriesByPartnerId(String deliveryPartnerId);

    // Find deliveries by multiple order IDs (for merged orders)
    List<Delivery> findByOrderIdIn(List<String> orderIds);

    // Find deliveries by partner and date range
    List<Delivery> findByDeliveryPartnerIdAndCreatedAtBetween(String partnerId, LocalDateTime startDate, LocalDateTime endDate);
}