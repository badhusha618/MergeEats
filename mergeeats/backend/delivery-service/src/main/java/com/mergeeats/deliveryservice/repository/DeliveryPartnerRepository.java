package com.mergeeats.deliveryservice.repository;

import com.mergeeats.common.models.DeliveryPartner;
import com.mergeeats.common.models.DeliveryPartner.AvailabilityStatus;
import com.mergeeats.common.models.DeliveryPartner.VehicleType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPartnerRepository extends MongoRepository<DeliveryPartner, String> {

    // Basic queries
    Optional<DeliveryPartner> findByUserId(String userId);
    
    Optional<DeliveryPartner> findByEmail(String email);
    
    Optional<DeliveryPartner> findByPhoneNumber(String phoneNumber);
    
    List<DeliveryPartner> findByIsActiveTrue();
    
    List<DeliveryPartner> findByIsVerifiedTrue();
    
    List<DeliveryPartner> findByIsActiveTrueAndIsVerifiedTrue();

    // Availability queries
    List<DeliveryPartner> findByAvailabilityStatus(AvailabilityStatus status);
    
    List<DeliveryPartner> findByAvailabilityStatusAndIsActiveTrueAndIsVerifiedTrue(AvailabilityStatus status);
    
    @Query("{ 'availabilityStatus': ?0, 'isActive': true, 'isVerified': true, 'activeOrderIds': { $size: { $lt: ?1 } } }")
    List<DeliveryPartner> findAvailablePartnersWithCapacity(AvailabilityStatus status, int maxOrders);

    // Vehicle type queries
    List<DeliveryPartner> findByVehicleType(VehicleType vehicleType);
    
    List<DeliveryPartner> findByVehicleTypeAndAvailabilityStatusAndIsActiveTrueAndIsVerifiedTrue(
        VehicleType vehicleType, AvailabilityStatus status);

    // Location-based queries
    @Query("{ 'currentLocation.latitude': { $gte: ?0, $lte: ?1 }, " +
           "'currentLocation.longitude': { $gte: ?2, $lte: ?3 }, " +
           "'isActive': true, 'isVerified': true }")
    List<DeliveryPartner> findPartnersInArea(double minLat, double maxLat, double minLon, double maxLon);
    
    @Query("{ 'currentLocation.latitude': { $gte: ?0, $lte: ?1 }, " +
           "'currentLocation.longitude': { $gte: ?2, $lte: ?3 }, " +
           "'availabilityStatus': ?4, 'isActive': true, 'isVerified': true }")
    List<DeliveryPartner> findAvailablePartnersInArea(double minLat, double maxLat, double minLon, double maxLon, AvailabilityStatus status);

    // Rating queries
    List<DeliveryPartner> findByRatingGreaterThanEqual(Double minRating);
    
    @Query("{ 'rating': { $gte: ?0 }, 'availabilityStatus': ?1, 'isActive': true, 'isVerified': true }")
    List<DeliveryPartner> findByRatingAndAvailabilityStatus(Double minRating, AvailabilityStatus status);

    // Order capacity queries
    @Query("{ 'activeOrderIds': { $size: { $lt: '$maxConcurrentOrders' } }, 'isActive': true, 'isVerified': true }")
    List<DeliveryPartner> findPartnersWithAvailableCapacity();
    
    @Query("{ 'activeOrderIds': { $in: [?0] } }")
    List<DeliveryPartner> findByActiveOrderId(String orderId);

    // Performance queries
    List<DeliveryPartner> findByCompletedDeliveriesGreaterThan(Integer minDeliveries);
    
    @Query("{ 'totalDeliveries': { $gt: 0 }, 'completedDeliveries': { $gte: { $multiply: ['$totalDeliveries', ?0] } } }")
    List<DeliveryPartner> findByCompletionRateGreaterThan(double minCompletionRate);

    // Time-based queries
    List<DeliveryPartner> findByLastActiveTimeBefore(LocalDateTime dateTime);
    
    List<DeliveryPartner> findByLastActiveTimeAfter(LocalDateTime dateTime);
    
    List<DeliveryPartner> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Statistics queries
    @Query(value = "{ 'isActive': true, 'isVerified': true }", count = true)
    long countActiveVerifiedPartners();
    
    @Query(value = "{ 'availabilityStatus': ?0 }", count = true)
    long countByAvailabilityStatus(AvailabilityStatus status);
    
    @Query(value = "{ 'vehicleType': ?0, 'isActive': true }", count = true)
    long countByVehicleTypeAndActive(VehicleType vehicleType);

    // Complex queries for partner assignment
    @Query("{ 'currentLocation.latitude': { $gte: ?0, $lte: ?1 }, " +
           "'currentLocation.longitude': { $gte: ?2, $lte: ?3 }, " +
           "'availabilityStatus': 'AVAILABLE', " +
           "'isActive': true, 'isVerified': true, " +
           "'rating': { $gte: ?4 }, " +
           "'activeOrderIds': { $size: { $lt: '$maxConcurrentOrders' } } }")
    List<DeliveryPartner> findOptimalPartnersInArea(double minLat, double maxLat, double minLon, double maxLon, double minRating);
    
    // Delivery radius queries
    @Query("{ 'deliveryRadius': { $gte: ?0 }, 'isActive': true, 'isVerified': true }")
    List<DeliveryPartner> findByMinimumDeliveryRadius(Double minRadius);
}