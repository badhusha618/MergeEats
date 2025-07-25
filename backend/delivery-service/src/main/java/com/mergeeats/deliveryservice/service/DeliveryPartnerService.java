package com.mergeeats.deliveryservice.service;

import com.mergeeats.common.models.Address;
import com.mergeeats.common.models.DeliveryPartner;
import com.mergeeats.deliveryservice.dto.CreateDeliveryPartnerRequest;
import com.mergeeats.deliveryservice.dto.LocationUpdateRequest;
import com.mergeeats.deliveryservice.repository.DeliveryPartnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class DeliveryPartnerService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryPartnerService.class);

    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${delivery.assignment.max-distance-km:10.0}")
    private Double maxAssignmentDistance;

    @Value("${delivery.assignment.max-orders-per-partner:5}")
    private Integer maxOrdersPerPartner;

    @Value("${delivery.tracking.location-cache-ttl-minutes:5}")
    private Long locationCacheTtl;

    // Create delivery partner
    public DeliveryPartner createDeliveryPartner(CreateDeliveryPartnerRequest request) {
        logger.info("Creating delivery partner for user: {}", request.getUserId());

        // Check if partner already exists
        Optional<DeliveryPartner> existingPartner = deliveryPartnerRepository.findByUserId(request.getUserId());
        if (existingPartner.isPresent()) {
            throw new RuntimeException("Delivery partner already exists for user: " + request.getUserId());
        }

        // Check if email already exists
        Optional<DeliveryPartner> existingEmail = deliveryPartnerRepository.findByEmail(request.getEmail());
        if (existingEmail.isPresent()) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
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

        if (request.getCurrentLocation() != null) {
            partner.setCurrentLocation(request.getCurrentLocation());
            partner.setLastLocationUpdate(LocalDateTime.now());
        }

        DeliveryPartner savedPartner = deliveryPartnerRepository.save(partner);

        // Cache partner location
        cachePartnerLocation(savedPartner.getPartnerId(), request.getCurrentLocation());

        // Publish event
        publishPartnerEvent("PARTNER_CREATED", savedPartner);

        logger.info("Delivery partner created successfully: {}", savedPartner.getPartnerId());
        return savedPartner;
    }

    // Get delivery partner by ID
    public DeliveryPartner getDeliveryPartnerById(String partnerId) {
        return deliveryPartnerRepository.findById(partnerId)
            .orElseThrow(() -> new RuntimeException("Delivery partner not found: " + partnerId));
    }

    // Get delivery partner by user ID
    public DeliveryPartner getDeliveryPartnerByUserId(String userId) {
        return deliveryPartnerRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Delivery partner not found for user: " + userId));
    }

    // Update partner availability
    public DeliveryPartner updateAvailability(String partnerId, Boolean isAvailable) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);
        partner.setIsAvailable(isAvailable);
        partner.setLastActiveTime(LocalDateTime.now());

        DeliveryPartner updatedPartner = deliveryPartnerRepository.save(partner);

        // Publish event
        publishPartnerEvent("PARTNER_AVAILABILITY_UPDATED", updatedPartner);

        logger.info("Partner availability updated: {} - {}", partnerId, isAvailable);
        return updatedPartner;
    }

    // Update partner online status
    public DeliveryPartner updateOnlineStatus(String partnerId, Boolean isOnline) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);
        partner.setIsOnline(isOnline);
        partner.setLastActiveTime(LocalDateTime.now());

        if (!isOnline) {
            partner.setIsAvailable(false); // Offline partners can't be available
        }

        DeliveryPartner updatedPartner = deliveryPartnerRepository.save(partner);

        // Publish event
        publishPartnerEvent("PARTNER_ONLINE_STATUS_UPDATED", updatedPartner);

        logger.info("Partner online status updated: {} - {}", partnerId, isOnline);
        return updatedPartner;
    }

    // Update partner location
    public DeliveryPartner updateLocation(String partnerId, LocationUpdateRequest request) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);

        Address location = new Address();
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());

        partner.setCurrentLocation(location);
        partner.setLastLocationUpdate(LocalDateTime.now());

        DeliveryPartner updatedPartner = deliveryPartnerRepository.save(partner);

        // Cache location for real-time tracking
        cachePartnerLocation(partnerId, location);

        // Publish location update event
        Map<String, Object> locationEvent = new HashMap<>();
        locationEvent.put("partnerId", partnerId);
        locationEvent.put("latitude", request.getLatitude());
        locationEvent.put("longitude", request.getLongitude());
        locationEvent.put("status", request.getStatus());
        locationEvent.put("timestamp", LocalDateTime.now());

        kafkaTemplate.send("partner-location-updates", locationEvent);

        return updatedPartner;
    }

    // Find available partners in area
    public List<DeliveryPartner> findAvailablePartnersInArea(Double centerLat, Double centerLng, Double radiusKm) {
        // Calculate bounding box
        Double latRange = radiusKm / 111.0; // Approximate km to degree conversion
        Double lngRange = radiusKm / (111.0 * Math.cos(Math.toRadians(centerLat)));

        Double minLat = centerLat - latRange;
        Double maxLat = centerLat + latRange;
        Double minLng = centerLng - lngRange;
        Double maxLng = centerLng + lngRange;

        List<DeliveryPartner> partners = deliveryPartnerRepository.findAvailablePartnersInArea(minLat, maxLat, minLng, maxLng);

        // Filter by exact distance and capacity
        return partners.stream()
            .filter(partner -> {
                if (partner.getCurrentLocation() == null) return false;
                
                double distance = calculateDistance(
                    centerLat, centerLng,
                    partner.getCurrentLocation().getLatitude(),
                    partner.getCurrentLocation().getLongitude()
                );
                
                int currentOrders = partner.getCurrentOrderIds() != null ? partner.getCurrentOrderIds().size() : 0;
                
                return distance <= radiusKm && currentOrders < maxOrdersPerPartner;
            })
            .sorted((p1, p2) -> {
                // Sort by rating (descending) then by current orders (ascending)
                int ratingCompare = Double.compare(p2.getRating(), p1.getRating());
                if (ratingCompare != 0) return ratingCompare;
                
                int orders1 = p1.getCurrentOrderIds() != null ? p1.getCurrentOrderIds().size() : 0;
                int orders2 = p2.getCurrentOrderIds() != null ? p2.getCurrentOrderIds().size() : 0;
                return Integer.compare(orders1, orders2);
            })
            .toList();
    }

    // Find best partner for delivery
    public DeliveryPartner findBestPartnerForDelivery(Double pickupLat, Double pickupLng) {
        List<DeliveryPartner> availablePartners = findAvailablePartnersInArea(pickupLat, pickupLng, maxAssignmentDistance);
        
        if (availablePartners.isEmpty()) {
            throw new RuntimeException("No available delivery partners found in the area");
        }

        // Return the best partner (already sorted by rating and current orders)
        return availablePartners.get(0);
    }

    // Assign order to partner
    public DeliveryPartner assignOrderToPartner(String partnerId, String orderId) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);

        List<String> currentOrders = partner.getCurrentOrderIds();
        if (currentOrders == null) {
            currentOrders = new ArrayList<>();
        }

        if (currentOrders.size() >= maxOrdersPerPartner) {
            throw new RuntimeException("Partner has reached maximum order capacity");
        }

        currentOrders.add(orderId);
        partner.setCurrentOrderIds(currentOrders);

        // Update availability if at capacity
        if (currentOrders.size() >= maxOrdersPerPartner) {
            partner.setIsAvailable(false);
        }

        DeliveryPartner updatedPartner = deliveryPartnerRepository.save(partner);

        // Publish event
        Map<String, Object> assignmentEvent = new HashMap<>();
        assignmentEvent.put("partnerId", partnerId);
        assignmentEvent.put("orderId", orderId);
        assignmentEvent.put("timestamp", LocalDateTime.now());
        kafkaTemplate.send("order-assignments", assignmentEvent);

        logger.info("Order {} assigned to partner {}", orderId, partnerId);
        return updatedPartner;
    }

    // Complete order for partner
    public DeliveryPartner completeOrderForPartner(String partnerId, String orderId) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);

        List<String> currentOrders = partner.getCurrentOrderIds();
        if (currentOrders != null) {
            currentOrders.remove(orderId);
            partner.setCurrentOrderIds(currentOrders);
        }

        // Update stats
        partner.setCompletedDeliveries(partner.getCompletedDeliveries() + 1);
        partner.setTotalDeliveries(partner.getTotalDeliveries() + 1);

        // Make available again if was at capacity
        if (!partner.getIsAvailable() && partner.getIsOnline()) {
            partner.setIsAvailable(true);
        }

        DeliveryPartner updatedPartner = deliveryPartnerRepository.save(partner);

        // Publish event
        publishPartnerEvent("ORDER_COMPLETED", updatedPartner);

        logger.info("Order {} completed by partner {}", orderId, partnerId);
        return updatedPartner;
    }

    // Update partner rating
    public DeliveryPartner updatePartnerRating(String partnerId, Double newRating) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);

        // Calculate weighted average rating
        double currentRating = partner.getRating();
        int totalDeliveries = partner.getCompletedDeliveries();

        if (totalDeliveries == 0) {
            partner.setRating(newRating);
        } else {
            double weightedRating = ((currentRating * totalDeliveries) + newRating) / (totalDeliveries + 1);
            partner.setRating(Math.round(weightedRating * 100.0) / 100.0); // Round to 2 decimal places
        }

        DeliveryPartner updatedPartner = deliveryPartnerRepository.save(partner);

        // Publish event
        publishPartnerEvent("PARTNER_RATING_UPDATED", updatedPartner);

        logger.info("Partner rating updated: {} - {}", partnerId, updatedPartner.getRating());
        return updatedPartner;
    }

    // Get partner statistics
    public Map<String, Object> getPartnerStatistics(String partnerId) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("partnerId", partnerId);
        stats.put("totalDeliveries", partner.getTotalDeliveries());
        stats.put("completedDeliveries", partner.getCompletedDeliveries());
        stats.put("cancelledDeliveries", partner.getCancelledDeliveries());
        stats.put("rating", partner.getRating());
        stats.put("totalEarnings", partner.getTotalEarnings());
        stats.put("todayEarnings", partner.getTodayEarnings());
        stats.put("currentOrders", partner.getCurrentOrderIds() != null ? partner.getCurrentOrderIds().size() : 0);
        stats.put("isAvailable", partner.getIsAvailable());
        stats.put("isOnline", partner.getIsOnline());

        return stats;
    }

    // Get all available partners
    public List<DeliveryPartner> getAvailablePartners() {
        return deliveryPartnerRepository.findByIsAvailableAndIsOnlineAndIsActive(true, true, true);
    }

    // Get online partners
    public List<DeliveryPartner> getOnlinePartners() {
        return deliveryPartnerRepository.findByIsOnlineAndIsActive(true, true);
    }

    // Deactivate partner
    public DeliveryPartner deactivatePartner(String partnerId) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);
        partner.setIsActive(false);
        partner.setIsAvailable(false);
        partner.setIsOnline(false);

        DeliveryPartner updatedPartner = deliveryPartnerRepository.save(partner);

        // Publish event
        publishPartnerEvent("PARTNER_DEACTIVATED", updatedPartner);

        logger.info("Partner deactivated: {}", partnerId);
        return updatedPartner;
    }

    // Helper method to calculate distance between two points
    private double calculateDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distance in km

        return distance;
    }

    // Helper method to cache partner location
    private void cachePartnerLocation(String partnerId, Address location) {
        if (location != null && location.getLatitude() != null && location.getLongitude() != null) {
            String key = "partner:location:" + partnerId;
            Map<String, Object> locationData = new HashMap<>();
            locationData.put("latitude", location.getLatitude());
            locationData.put("longitude", location.getLongitude());
            locationData.put("timestamp", LocalDateTime.now());

            redisTemplate.opsForValue().set(key, locationData, locationCacheTtl, TimeUnit.MINUTES);
        }
    }

    // Helper method to publish partner events
    private void publishPartnerEvent(String eventType, DeliveryPartner partner) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", eventType);
        event.put("partnerId", partner.getPartnerId());
        event.put("userId", partner.getUserId());
        event.put("isAvailable", partner.getIsAvailable());
        event.put("isOnline", partner.getIsOnline());
        event.put("isActive", partner.getIsActive());
        event.put("rating", partner.getRating());
        event.put("timestamp", LocalDateTime.now());

        kafkaTemplate.send("delivery-partner-events", event);
    }
}