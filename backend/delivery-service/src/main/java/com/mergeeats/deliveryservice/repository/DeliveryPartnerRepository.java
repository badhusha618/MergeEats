package com.mergeeats.deliveryservice.repository;

import com.mergeeats.common.models.DeliveryPartner;
import com.mergeeats.common.enums.DeliveryPartnerStatus;
import com.mergeeats.common.enums.VehicleType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPartnerRepository extends MongoRepository<DeliveryPartner, String> {

    // Find by user
    Optional<DeliveryPartner> findByUserId(String userId);
    Optional<DeliveryPartner> findByEmail(String email);
    Optional<DeliveryPartner> findByPhoneNumber(String phoneNumber);

    // Find by status
    List<DeliveryPartner> findByStatus(DeliveryPartnerStatus status);
    List<DeliveryPartner> findByStatusIn(List<DeliveryPartnerStatus> statuses);

    // Find available partners
    @Query("{'status': 'ONLINE', 'isActive': true, 'isVerified': true}")
    List<DeliveryPartner> findAvailablePartners();

    // Find partners by vehicle type
    List<DeliveryPartner> findByVehicleType(VehicleType vehicleType);
    List<DeliveryPartner> findByVehicleTypeIn(List<VehicleType> vehicleTypes);

    // Find partners in service area
    List<DeliveryPartner> findByServiceAreasContaining(String area);

    // Find partners by location (within radius)
    @Query("{'currentLocation.latitude': {'$gte': ?0, '$lte': ?1}, " +
           "'currentLocation.longitude': {'$gte': ?2, '$lte': ?3}, " +
           "'status': 'ONLINE', 'isActive': true}")
    List<DeliveryPartner> findPartnersInArea(Double minLat, Double maxLat, Double minLng, Double maxLng);

    // Find verified partners
    List<DeliveryPartner> findByIsVerified(Boolean isVerified);
    List<DeliveryPartner> findByIsVerifiedAndIsActive(Boolean isVerified, Boolean isActive);

    // Find partners by rating
    @Query("{'rating': {'$gte': ?0}, 'totalDeliveries': {'$gte': ?1}}")
    List<DeliveryPartner> findByRatingGreaterThanEqualAndMinDeliveries(Double minRating, Integer minDeliveries);

    // Find top-rated partners
    @Query("{'rating': {'$gte': ?0}, 'totalDeliveries': {'$gte': ?1}, 'isActive': true, 'isVerified': true}")
    List<DeliveryPartner> findTopRatedPartners(Double minRating, Integer minDeliveries);

    // Find partners by activity
    List<DeliveryPartner> findByIsActive(Boolean isActive);
    List<DeliveryPartner> findByLastActiveTimeAfter(LocalDateTime after);

    // Find partners for assignment (available and suitable)
    @Query("{'status': 'ONLINE', 'isActive': true, 'isVerified': true, " +
           "'currentLocation.latitude': {'$gte': ?0, '$lte': ?1}, " +
           "'currentLocation.longitude': {'$gte': ?2, '$lte': ?3}}")
    List<DeliveryPartner> findPartnersForAssignment(Double minLat, Double maxLat, Double minLng, Double maxLng);

    // Find partners with vehicle suitable for distance
    @Query("{'status': 'ONLINE', 'isActive': true, 'isVerified': true, " +
           "'vehicleType': {'$in': ?0}}")
    List<DeliveryPartner> findPartnersByVehicleTypes(List<VehicleType> suitableVehicles);

    // Statistics queries
    long countByStatus(DeliveryPartnerStatus status);
    long countByIsVerified(Boolean isVerified);
    long countByIsActive(Boolean isActive);

    @Query("{'createdAt': {'$gte': ?0, '$lte': ?1}}")
    long countPartnersJoinedInPeriod(LocalDateTime start, LocalDateTime end);

    // Performance queries
    @Query("{'totalDeliveries': {'$gte': ?0}, 'rating': {'$gte': ?1}}")
    List<DeliveryPartner> findHighPerformingPartners(Integer minDeliveries, Double minRating);

    @Query("{'totalEarnings': {'$gte': ?0}, 'totalDeliveries': {'$gte': ?1}}")
    List<DeliveryPartner> findTopEarningPartners(Double minEarnings, Integer minDeliveries);

    // Find partners needing verification
    @Query("{'isVerified': false, 'isActive': true}")
    List<DeliveryPartner> findPartnersNeedingVerification();

    // Find inactive partners
    @Query("{'lastActiveTime': {'$lt': ?0}, 'isActive': true}")
    List<DeliveryPartner> findInactivePartners(LocalDateTime cutoffTime);

    // Find partners by vehicle number (for verification)
    Optional<DeliveryPartner> findByVehicleNumber(String vehicleNumber);
    Optional<DeliveryPartner> findByLicenseNumber(String licenseNumber);
}