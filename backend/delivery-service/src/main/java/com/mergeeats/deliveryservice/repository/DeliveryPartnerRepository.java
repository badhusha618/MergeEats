package com.mergeeats.deliveryservice.repository;

import com.mergeeats.common.models.DeliveryPartner;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPartnerRepository extends MongoRepository<DeliveryPartner, String> {

    // Find by user ID
    Optional<DeliveryPartner> findByUserId(String userId);

    // Find by email
    Optional<DeliveryPartner> findByEmail(String email);

    // Find available partners
    List<DeliveryPartner> findByIsAvailableAndIsOnlineAndIsActive(Boolean isAvailable, Boolean isOnline, Boolean isActive);

    // Find online partners
    List<DeliveryPartner> findByIsOnlineAndIsActive(Boolean isOnline, Boolean isActive);

    // Find active partners
    List<DeliveryPartner> findByIsActive(Boolean isActive);

    // Find partners by vehicle type
    List<DeliveryPartner> findByVehicleTypeAndIsActive(String vehicleType, Boolean isActive);

    // Find partners by rating range
    @Query("{'rating': {'$gte': ?0, '$lte': ?1}, 'isActive': true}")
    List<DeliveryPartner> findByRatingBetween(Double minRating, Double maxRating);

    // Find partners in area (location-based query)
    @Query("{'currentLocation.latitude': {'$gte': ?0, '$lte': ?1}, " +
           "'currentLocation.longitude': {'$gte': ?2, '$lte': ?3}, " +
           "'isAvailable': true, 'isOnline': true, 'isActive': true}")
    List<DeliveryPartner> findAvailablePartnersInArea(Double minLat, Double maxLat, Double minLng, Double maxLng);

    // Find partners with less than max orders
    @Query("{'$expr': {'$lt': [{'$size': {'$ifNull': ['$currentOrderIds', []]}}, ?0]}, " +
           "'isAvailable': true, 'isOnline': true, 'isActive': true}")
    List<DeliveryPartner> findAvailablePartnersWithCapacity(Integer maxOrders);

    // Find top rated partners
    @Query("{'isActive': true}")
    List<DeliveryPartner> findTopRatedPartners();

    // Count active partners
    long countByIsActive(Boolean isActive);

    // Count online partners
    long countByIsOnlineAndIsActive(Boolean isOnline, Boolean isActive);

    // Count available partners
    long countByIsAvailableAndIsOnlineAndIsActive(Boolean isAvailable, Boolean isOnline, Boolean isActive);
}