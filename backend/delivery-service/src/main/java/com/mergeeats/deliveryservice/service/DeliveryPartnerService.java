package com.mergeeats.deliveryservice.service;

import com.mergeeats.common.models.DeliveryPartner;
import com.mergeeats.common.models.Address;
import com.mergeeats.common.enums.DeliveryPartnerStatus;
import com.mergeeats.common.enums.VehicleType;
import com.mergeeats.deliveryservice.repository.DeliveryPartnerRepository;
import com.mergeeats.deliveryservice.repository.DeliveryRepository;
import com.mergeeats.deliveryservice.dto.RegisterDeliveryPartnerRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class DeliveryPartnerService {

    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // Register new delivery partner
    public DeliveryPartner registerDeliveryPartner(RegisterDeliveryPartnerRequest request) {
        // Check if partner already exists
        Optional<DeliveryPartner> existingPartner = deliveryPartnerRepository.findByUserId(request.getUserId());
        if (existingPartner.isPresent()) {
            throw new RuntimeException("Delivery partner already registered for user: " + request.getUserId());
        }

        // Check for duplicate email
        Optional<DeliveryPartner> existingEmail = deliveryPartnerRepository.findByEmail(request.getEmail());
        if (existingEmail.isPresent()) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        // Check for duplicate vehicle number
        Optional<DeliveryPartner> existingVehicle = deliveryPartnerRepository.findByVehicleNumber(request.getVehicleNumber());
        if (existingVehicle.isPresent()) {
            throw new RuntimeException("Vehicle number already registered: " + request.getVehicleNumber());
        }

        DeliveryPartner partner = new DeliveryPartner(
            request.getUserId(),
            request.getFullName(),
            request.getEmail(),
            request.getPhoneNumber(),
            request.getVehicleType(),
            request.getVehicleNumber(),
            request.getLicenseNumber()
        );

        partner.setServiceAreas(request.getServiceAreas());
        partner = deliveryPartnerRepository.save(partner);

        // Publish event
        publishPartnerEvent("partner.registered", partner);

        return partner;
    }

    // Get delivery partner by ID
    public Optional<DeliveryPartner> getDeliveryPartnerById(String partnerId) {
        return deliveryPartnerRepository.findById(partnerId);
    }

    // Get delivery partner by user ID
    public Optional<DeliveryPartner> getDeliveryPartnerByUserId(String userId) {
        return deliveryPartnerRepository.findByUserId(userId);
    }

    // Update partner status
    public DeliveryPartner updatePartnerStatus(String partnerId, DeliveryPartnerStatus status) {
        Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
        if (partnerOpt.isEmpty()) {
            throw new RuntimeException("Delivery partner not found: " + partnerId);
        }

        DeliveryPartner partner = partnerOpt.get();
        DeliveryPartnerStatus oldStatus = partner.getStatus();
        partner.setStatus(status);
        partner.setLastActiveTime(LocalDateTime.now());

        partner = deliveryPartnerRepository.save(partner);

        // Publish status change event
        publishPartnerStatusEvent(partner, oldStatus, status);

        return partner;
    }

    // Update partner location
    public DeliveryPartner updatePartnerLocation(String partnerId, Address location) {
        Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
        if (partnerOpt.isEmpty()) {
            throw new RuntimeException("Delivery partner not found: " + partnerId);
        }

        DeliveryPartner partner = partnerOpt.get();
        partner.setCurrentLocation(location);
        partner.setLastActiveTime(LocalDateTime.now());

        partner = deliveryPartnerRepository.save(partner);

        // Publish location update
        publishLocationUpdate(partner);

        return partner;
    }

    // Verify delivery partner
    public DeliveryPartner verifyDeliveryPartner(String partnerId) {
        Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
        if (partnerOpt.isEmpty()) {
            throw new RuntimeException("Delivery partner not found: " + partnerId);
        }

        DeliveryPartner partner = partnerOpt.get();
        partner.setIsVerified(true);

        partner = deliveryPartnerRepository.save(partner);

        // Publish verification event
        publishPartnerEvent("partner.verified", partner);

        return partner;
    }

    // Activate/Deactivate partner
    public DeliveryPartner togglePartnerActivation(String partnerId, boolean isActive) {
        Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
        if (partnerOpt.isEmpty()) {
            throw new RuntimeException("Delivery partner not found: " + partnerId);
        }

        DeliveryPartner partner = partnerOpt.get();
        partner.setIsActive(isActive);

        if (!isActive) {
            partner.setStatus(DeliveryPartnerStatus.INACTIVE);
        }

        partner = deliveryPartnerRepository.save(partner);

        // Publish activation event
        publishPartnerEvent(isActive ? "partner.activated" : "partner.deactivated", partner);

        return partner;
    }

    // Get available partners
    public List<DeliveryPartner> getAvailablePartners() {
        return deliveryPartnerRepository.findAvailablePartners();
    }

    // Get partners by status
    public List<DeliveryPartner> getPartnersByStatus(DeliveryPartnerStatus status) {
        return deliveryPartnerRepository.findByStatus(status);
    }

    // Find partners in area for assignment
    public List<DeliveryPartner> findPartnersInArea(Double latitude, Double longitude, Double radiusKm) {
        double[] bounds = calculateSearchBounds(latitude, longitude, radiusKm);
        return deliveryPartnerRepository.findPartnersInArea(bounds[0], bounds[1], bounds[2], bounds[3]);
    }

    // Find best partner for delivery (used by DeliveryService)
    public DeliveryPartner findBestPartnerForDelivery(Double pickupLat, Double pickupLng) {
        List<DeliveryPartner> availablePartners = findPartnersInArea(pickupLat, pickupLng, 10.0);
        
        if (availablePartners.isEmpty()) {
            return null;
        }

        // Find partner with best combination of distance and rating
        return availablePartners.stream()
            .min((p1, p2) -> {
                double distance1 = calculateDistance(pickupLat, pickupLng, 
                    p1.getCurrentLocation().getLatitude(), p1.getCurrentLocation().getLongitude());
                double distance2 = calculateDistance(pickupLat, pickupLng, 
                    p2.getCurrentLocation().getLatitude(), p2.getCurrentLocation().getLongitude());
                
                // Weighted score: 70% distance, 30% rating
                double score1 = distance1 * 0.7 - (p1.getRating() * 0.3);
                double score2 = distance2 * 0.7 - (p2.getRating() * 0.3);
                
                return Double.compare(score1, score2);
            })
            .orElse(null);
    }

    // Get partner statistics
    public Map<String, Object> getPartnerStatistics(String partnerId) {
        Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
        if (partnerOpt.isEmpty()) {
            throw new RuntimeException("Delivery partner not found: " + partnerId);
        }

        DeliveryPartner partner = partnerOpt.get();
        Map<String, Object> stats = new HashMap<>();

        stats.put("partnerId", partnerId);
        stats.put("fullName", partner.getFullName());
        stats.put("status", partner.getStatus().name());
        stats.put("rating", partner.getRating());
        stats.put("totalDeliveries", partner.getTotalDeliveries());
        stats.put("totalEarnings", partner.getTotalEarnings());
        stats.put("isVerified", partner.getIsVerified());
        stats.put("isActive", partner.getIsActive());
        stats.put("vehicleType", partner.getVehicleType().name());
        stats.put("joinedDate", partner.getCreatedAt());
        stats.put("lastActiveTime", partner.getLastActiveTime());

        // Calculate additional metrics
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long monthlyDeliveries = deliveryRepository.findCompletedDeliveriesByPartnerInPeriod(
            partnerId, monthStart, LocalDateTime.now()).size();
        stats.put("monthlyDeliveries", monthlyDeliveries);

        // Calculate average delivery time (if available)
        // This would require additional tracking in the delivery model
        stats.put("averageDeliveryTime", "N/A");

        return stats;
    }

    // Get delivery statistics (system-wide)
    public Map<String, Object> getDeliveryStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Partner statistics
        long totalPartners = deliveryPartnerRepository.count();
        long activePartners = deliveryPartnerRepository.countByIsActive(true);
        long verifiedPartners = deliveryPartnerRepository.countByIsVerified(true);
        long onlinePartners = deliveryPartnerRepository.countByStatus(DeliveryPartnerStatus.ONLINE);

        stats.put("totalPartners", totalPartners);
        stats.put("activePartners", activePartners);
        stats.put("verifiedPartners", verifiedPartners);
        stats.put("onlinePartners", onlinePartners);

        // Vehicle type distribution
        Map<String, Long> vehicleDistribution = new HashMap<>();
        for (VehicleType vehicleType : VehicleType.values()) {
            long count = deliveryPartnerRepository.findByVehicleType(vehicleType).size();
            vehicleDistribution.put(vehicleType.name(), count);
        }
        stats.put("vehicleDistribution", vehicleDistribution);

        // Delivery statistics
        long totalDeliveries = deliveryRepository.count();
        stats.put("totalDeliveries", totalDeliveries);

        // Top performing partners
        List<DeliveryPartner> topPartners = deliveryPartnerRepository.findTopRatedPartners(4.0, 10);
        stats.put("topPerformingPartners", topPartners.size());

        return stats;
    }

    // Get partners needing verification
    public List<DeliveryPartner> getPartnersNeedingVerification() {
        return deliveryPartnerRepository.findPartnersNeedingVerification();
    }

    // Get inactive partners
    public List<DeliveryPartner> getInactivePartners(int daysInactive) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysInactive);
        return deliveryPartnerRepository.findInactivePartners(cutoff);
    }

    // Update partner rating (called after delivery completion)
    public void updatePartnerRating(String partnerId, Double newRating) {
        Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
        if (partnerOpt.isPresent()) {
            DeliveryPartner partner = partnerOpt.get();
            
            // Calculate weighted average rating
            double currentRating = partner.getRating();
            int totalDeliveries = partner.getTotalDeliveries();
            
            if (totalDeliveries > 0) {
                double weightedRating = ((currentRating * totalDeliveries) + newRating) / (totalDeliveries + 1);
                partner.setRating(Math.round(weightedRating * 100.0) / 100.0);
            } else {
                partner.setRating(newRating);
            }
            
            deliveryPartnerRepository.save(partner);
        }
    }

    // Helper method to calculate distance
    private double calculateDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return Double.MAX_VALUE;
        }

        // Haversine formula
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLatRad = Math.toRadians(lat2 - lat1);
        double deltaLngRad = Math.toRadians(lng2 - lng1);

        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLngRad / 2) * Math.sin(deltaLngRad / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371.0 * c; // Earth's radius in kilometers
    }

    // Helper method to calculate search bounds
    private double[] calculateSearchBounds(Double lat, Double lng, Double radiusKm) {
        double latOffset = radiusKm / 111.0; // Approximate km per degree latitude
        double lngOffset = radiusKm / (111.0 * Math.cos(Math.toRadians(lat))); // Adjust for longitude

        return new double[] {
            lat - latOffset,  // minLat
            lat + latOffset,  // maxLat
            lng - lngOffset,  // minLng
            lng + lngOffset   // maxLng
        };
    }

    // Publish partner event to Kafka
    private void publishPartnerEvent(String eventType, DeliveryPartner partner) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventType);
            event.put("partnerId", partner.getPartnerId());
            event.put("userId", partner.getUserId());
            event.put("fullName", partner.getFullName());
            event.put("status", partner.getStatus().name());
            event.put("isActive", partner.getIsActive());
            event.put("isVerified", partner.getIsVerified());
            event.put("timestamp", LocalDateTime.now());

            kafkaTemplate.send("delivery-partner-events", event);
        } catch (Exception e) {
            System.err.println("Failed to publish partner event: " + e.getMessage());
        }
    }

    // Publish partner status change event
    private void publishPartnerStatusEvent(DeliveryPartner partner, DeliveryPartnerStatus oldStatus, DeliveryPartnerStatus newStatus) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "partner.status.changed");
            event.put("partnerId", partner.getPartnerId());
            event.put("oldStatus", oldStatus.name());
            event.put("newStatus", newStatus.name());
            event.put("timestamp", LocalDateTime.now());

            kafkaTemplate.send("delivery-partner-status-updates", event);
        } catch (Exception e) {
            System.err.println("Failed to publish partner status event: " + e.getMessage());
        }
    }

    // Publish location update
    private void publishLocationUpdate(DeliveryPartner partner) {
        try {
            Map<String, Object> locationUpdate = new HashMap<>();
            locationUpdate.put("partnerId", partner.getPartnerId());
            locationUpdate.put("currentLocation", partner.getCurrentLocation());
            locationUpdate.put("timestamp", partner.getLastActiveTime());

            kafkaTemplate.send("delivery-partner-location-updates", locationUpdate);
        } catch (Exception e) {
            System.err.println("Failed to publish location update: " + e.getMessage());
        }
    }
}