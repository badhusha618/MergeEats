package com.mergeeats.deliveryservice.service;

import com.mergeeats.common.models.Delivery;
import com.mergeeats.common.models.DeliveryUpdate;
import com.mergeeats.common.enums.DeliveryStatus;
import com.mergeeats.deliveryservice.repository.DeliveryRepository;
import com.mergeeats.deliveryservice.dto.CreateDeliveryRequest;
import com.mergeeats.deliveryservice.dto.UpdateLocationRequest;
import com.mergeeats.deliveryservice.dto.AssignDeliveryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${delivery.assignment.max-distance-km:10.0}")
    private double maxAssignmentDistance;

    @Value("${delivery.assignment.max-orders-per-partner:5}")
    private int maxOrdersPerPartner;

    @Value("${delivery.assignment.auto-assignment-enabled:true}")
    private boolean autoAssignmentEnabled;

    @Value("${delivery.tracking.location-cache-ttl-minutes:5}")
    private int locationCacheTtl;

    // Create new delivery
    public Delivery createDelivery(CreateDeliveryRequest request) {
        Delivery delivery = new Delivery(
            request.getOrderId(),
            request.getCustomerId(),
            request.getRestaurantId(),
            request.getPickupAddress(),
            request.getDeliveryAddress(),
            request.getOrderTotal(),
            request.getScheduledPickupTime(),
            request.getEstimatedDeliveryTime()
        );

        delivery.setDeliveryFee(request.getDeliveryFee());
        delivery.setDeliveryInstructions(request.getDeliveryInstructions());
        delivery.setCustomerPhone(request.getCustomerPhone());
        delivery.setRestaurantPhone(request.getRestaurantPhone());
        delivery.setTrackingNumber(generateTrackingNumber());

        // Calculate estimated distance
        double distance = calculateDistance(
            request.getPickupAddress().getLatitude(),
            request.getPickupAddress().getLongitude(),
            request.getDeliveryAddress().getLatitude(),
            request.getDeliveryAddress().getLongitude()
        );
        delivery.setEstimatedDistance(distance);

        delivery = deliveryRepository.save(delivery);

        // Add initial tracking update
        addTrackingUpdate(delivery, DeliveryStatus.PENDING, "Delivery created and pending assignment");

        // Publish delivery created event
        publishDeliveryEvent("delivery.created", delivery);

        // Auto-assign if enabled
        if (autoAssignmentEnabled) {
            autoAssignDelivery(delivery.getDeliveryId());
        }

        return delivery;
    }

    // Get delivery by ID
    public Optional<Delivery> getDeliveryById(String deliveryId) {
        return deliveryRepository.findById(deliveryId);
    }

    // Get delivery by order ID
    public Optional<Delivery> getDeliveryByOrderId(String orderId) {
        return deliveryRepository.findByOrderId(orderId);
    }

    // Get deliveries by delivery partner
    public List<Delivery> getDeliveriesByPartnerId(String partnerId) {
        return deliveryRepository.findByDeliveryPartnerId(partnerId);
    }

    // Get active deliveries for partner
    public List<Delivery> getActiveDeliveriesForPartner(String partnerId) {
        return deliveryRepository.findActiveDeliveriesByPartnerId(partnerId);
    }

    // Assign delivery to partner
    public Delivery assignDelivery(String deliveryId, AssignDeliveryRequest request) {
        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        if (deliveryOpt.isEmpty()) {
            throw new RuntimeException("Delivery not found with ID: " + deliveryId);
        }

        Delivery delivery = deliveryOpt.get();
        
        if (delivery.getStatus() != DeliveryStatus.PENDING) {
            throw new RuntimeException("Delivery is not in pending status");
        }

        // Check if partner can take more deliveries
        long activeDeliveries = deliveryRepository.countActiveDeliveriesByPartnerId(request.getDeliveryPartnerId());
        if (activeDeliveries >= maxOrdersPerPartner) {
            throw new RuntimeException("Delivery partner has reached maximum active deliveries");
        }

        delivery.setDeliveryPartnerId(request.getDeliveryPartnerId());
        delivery.setDeliveryPartnerPhone(request.getDeliveryPartnerPhone());
        delivery.setStatus(DeliveryStatus.ASSIGNED);

        delivery = deliveryRepository.save(delivery);

        // Add tracking update
        addTrackingUpdate(delivery, DeliveryStatus.ASSIGNED, 
            "Delivery assigned to partner: " + request.getDeliveryPartnerId());

        // Publish assignment event
        publishDeliveryEvent("delivery.assigned", delivery);

        return delivery;
    }

    // Auto-assign delivery to nearest available partner
    public boolean autoAssignDelivery(String deliveryId) {
        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        if (deliveryOpt.isEmpty()) {
            return false;
        }

        Delivery delivery = deliveryOpt.get();
        if (delivery.getStatus() != DeliveryStatus.PENDING) {
            return false;
        }

        // Find available partners near pickup location
        double pickupLat = delivery.getPickupAddress().getLatitude();
        double pickupLng = delivery.getPickupAddress().getLongitude();
        double maxDistanceMeters = maxAssignmentDistance * 1000; // Convert to meters

        // This would typically query a separate delivery partner service
        // For now, we'll simulate finding available partners
        List<String> availablePartners = findAvailablePartnersNearLocation(pickupLat, pickupLng, maxDistanceMeters);

        if (!availablePartners.isEmpty()) {
            String selectedPartnerId = availablePartners.get(0); // Simple selection logic
            
            AssignDeliveryRequest assignRequest = new AssignDeliveryRequest();
            assignRequest.setDeliveryPartnerId(selectedPartnerId);
            assignRequest.setDeliveryPartnerPhone(""); // Would be fetched from partner service
            
            assignDelivery(deliveryId, assignRequest);
            return true;
        }

        return false;
    }

    // Update delivery status
    public Delivery updateDeliveryStatus(String deliveryId, DeliveryStatus newStatus, String message) {
        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        if (deliveryOpt.isEmpty()) {
            throw new RuntimeException("Delivery not found with ID: " + deliveryId);
        }

        Delivery delivery = deliveryOpt.get();
        
        if (!delivery.getStatus().canTransitionTo(newStatus)) {
            throw new RuntimeException("Invalid status transition from " + delivery.getStatus() + " to " + newStatus);
        }

        delivery.setStatus(newStatus);

        // Set timestamps based on status
        LocalDateTime now = LocalDateTime.now();
        switch (newStatus) {
            case PICKED_UP:
                delivery.setActualPickupTime(now);
                break;
            case DELIVERED:
                delivery.setActualDeliveryTime(now);
                break;
        }

        delivery = deliveryRepository.save(delivery);

        // Add tracking update
        addTrackingUpdate(delivery, newStatus, message);

        // Publish status update event
        publishDeliveryEvent("delivery.status.updated", delivery);

        return delivery;
    }

    // Update delivery location
    public void updateDeliveryLocation(String deliveryId, UpdateLocationRequest request) {
        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        if (deliveryOpt.isEmpty()) {
            throw new RuntimeException("Delivery not found with ID: " + deliveryId);
        }

        Delivery delivery = deliveryOpt.get();
        delivery.updateLocation(request.getLongitude(), request.getLatitude());
        deliveryRepository.save(delivery);

        // Cache location in Redis for real-time tracking
        String cacheKey = "delivery:location:" + deliveryId;
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", request.getLatitude());
        locationData.put("longitude", request.getLongitude());
        locationData.put("timestamp", LocalDateTime.now());
        
        redisTemplate.opsForValue().set(cacheKey, locationData, locationCacheTtl, TimeUnit.MINUTES);

        // Publish location update event
        publishDeliveryEvent("delivery.location.updated", delivery);
    }

    // Get real-time location from cache
    public Map<String, Object> getDeliveryLocation(String deliveryId) {
        String cacheKey = "delivery:location:" + deliveryId;
        return (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
    }

    // Cancel delivery
    public Delivery cancelDelivery(String deliveryId, String reason) {
        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        if (deliveryOpt.isEmpty()) {
            throw new RuntimeException("Delivery not found with ID: " + deliveryId);
        }

        Delivery delivery = deliveryOpt.get();
        
        if (delivery.getStatus().isTerminal()) {
            throw new RuntimeException("Cannot cancel delivery in terminal status: " + delivery.getStatus());
        }

        delivery.setStatus(DeliveryStatus.CANCELLED);
        delivery = deliveryRepository.save(delivery);

        // Add tracking update
        addTrackingUpdate(delivery, DeliveryStatus.CANCELLED, "Delivery cancelled: " + reason);

        // Publish cancellation event
        publishDeliveryEvent("delivery.cancelled", delivery);

        return delivery;
    }

    // Get deliveries by status
    public List<Delivery> getDeliveriesByStatus(DeliveryStatus status) {
        return deliveryRepository.findByStatus(status);
    }

    // Get overdue deliveries
    public List<Delivery> getOverdueDeliveries() {
        return deliveryRepository.findOverdueDeliveries(LocalDateTime.now());
    }

    // Get delivery statistics for partner
    public Map<String, Object> getPartnerStatistics(String partnerId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Delivery> deliveries = deliveryRepository.findByDeliveryPartnerIdAndCreatedAtBetween(
            partnerId, startDate, endDate);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDeliveries", deliveries.size());
        stats.put("completedDeliveries", deliveries.stream().filter(d -> d.getStatus() == DeliveryStatus.DELIVERED).count());
        stats.put("cancelledDeliveries", deliveries.stream().filter(d -> d.getStatus() == DeliveryStatus.CANCELLED).count());
        stats.put("averageRating", deliveries.stream()
            .filter(d -> d.getCustomerRating() != null)
            .mapToInt(Delivery::getCustomerRating)
            .average().orElse(0.0));
        stats.put("totalEarnings", deliveries.stream()
            .filter(d -> d.getStatus() == DeliveryStatus.DELIVERED)
            .mapToDouble(Delivery::getDeliveryFee)
            .sum());

        return stats;
    }

    // Batch delivery assignment
    public List<Delivery> createBatchDelivery(List<String> orderIds, String partnerId) {
        List<Delivery> deliveries = deliveryRepository.findByOrderIdIn(orderIds);
        
        if (deliveries.size() != orderIds.size()) {
            throw new RuntimeException("Some orders not found for batch delivery");
        }

        String batchId = UUID.randomUUID().toString();
        
        for (Delivery delivery : deliveries) {
            if (delivery.getStatus() != DeliveryStatus.PENDING) {
                throw new RuntimeException("All deliveries must be in pending status for batch assignment");
            }
            
            delivery.setDeliveryPartnerId(partnerId);
            delivery.setIsBatchDelivery(true);
            delivery.setBatchId(batchId);
            delivery.setBatchOrderIds(orderIds);
            delivery.setStatus(DeliveryStatus.ASSIGNED);
        }

        deliveries = deliveryRepository.saveAll(deliveries);

        // Publish batch assignment event
        Map<String, Object> batchData = new HashMap<>();
        batchData.put("batchId", batchId);
        batchData.put("partnerId", partnerId);
        batchData.put("deliveries", deliveries);
        kafkaTemplate.send("delivery.batch.assigned", batchData);

        return deliveries;
    }

    // Helper methods
    private void addTrackingUpdate(Delivery delivery, DeliveryStatus status, String message) {
        if (delivery.getTrackingUpdates() == null) {
            delivery.setTrackingUpdates(new ArrayList<>());
        }

        DeliveryUpdate update = new DeliveryUpdate(status, message, LocalDateTime.now());
        if (delivery.getCurrentLocation() != null) {
            update.setLocation(delivery.getCurrentLocation());
        }

        delivery.getTrackingUpdates().add(update);
        deliveryRepository.save(delivery);
    }

    private void publishDeliveryEvent(String eventType, Delivery delivery) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", eventType);
        event.put("deliveryId", delivery.getDeliveryId());
        event.put("orderId", delivery.getOrderId());
        event.put("status", delivery.getStatus());
        event.put("timestamp", LocalDateTime.now());
        
        kafkaTemplate.send("delivery.events", event);
    }

    private String generateTrackingNumber() {
        return "TRK" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    private List<String> findAvailablePartnersNearLocation(double latitude, double longitude, double maxDistanceMeters) {
        // This would typically call an external delivery partner service
        // For now, return a mock list
        return Arrays.asList("partner1", "partner2", "partner3");
    }
}