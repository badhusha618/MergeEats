package com.mergeeats.deliveryservice.service;

import com.mergeeats.common.models.Delivery;
import com.mergeeats.common.models.DeliveryPartner;
import com.mergeeats.deliveryservice.dto.CreateDeliveryRequest;
import com.mergeeats.deliveryservice.dto.LocationUpdateRequest;
import com.mergeeats.deliveryservice.repository.DeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryService.class);

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryPartnerService deliveryPartnerService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${delivery.assignment.auto-assignment-enabled:true}")
    private Boolean autoAssignmentEnabled;

    @Value("${delivery.route.optimization-enabled:true}")
    private Boolean routeOptimizationEnabled;

    @Value("${delivery.route.max-waypoints:25}")
    private Integer maxWaypoints;

    // Create delivery
    public Delivery createDelivery(CreateDeliveryRequest request) {
        logger.info("Creating delivery for order: {}", request.getOrderId());

        // Check if delivery already exists for this order
        Optional<Delivery> existingDelivery = deliveryRepository.findByOrderId(request.getOrderId());
        if (existingDelivery.isPresent()) {
            throw new RuntimeException("Delivery already exists for order: " + request.getOrderId());
        }

        Delivery delivery = new Delivery(
            request.getOrderId(),
            null, // Will be assigned later
            request.getCustomerId(),
            request.getRestaurantId(),
            request.getPickupAddress(),
            request.getDeliveryAddress()
        );

        delivery.setDeliveryFee(request.getDeliveryFee());
        delivery.setEstimatedDeliveryTimeMinutes(request.getEstimatedDeliveryTimeMinutes());
        delivery.setMergedOrderIds(request.getMergedOrderIds());
        delivery.setSpecialInstructions(request.getSpecialInstructions());
        delivery.setTrackingCode(generateTrackingCode());

        // Calculate distance
        if (request.getPickupAddress() != null && request.getDeliveryAddress() != null) {
            double distance = calculateDistance(
                request.getPickupAddress().getLatitude(),
                request.getPickupAddress().getLongitude(),
                request.getDeliveryAddress().getLatitude(),
                request.getDeliveryAddress().getLongitude()
            );
            delivery.setDistanceKm(distance);
        }

        Delivery savedDelivery = deliveryRepository.save(delivery);

        // Auto-assign if enabled
        if (autoAssignmentEnabled) {
            try {
                assignDeliveryPartner(savedDelivery.getDeliveryId());
            } catch (Exception e) {
                logger.warn("Failed to auto-assign delivery partner for delivery: {}", savedDelivery.getDeliveryId(), e);
            }
        }

        // Publish event
        publishDeliveryEvent("DELIVERY_CREATED", savedDelivery);

        logger.info("Delivery created successfully: {}", savedDelivery.getDeliveryId());
        return savedDelivery;
    }

    // Assign delivery partner
    public Delivery assignDeliveryPartner(String deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);

        if (delivery.getDeliveryPartnerId() != null) {
            throw new RuntimeException("Delivery already assigned to partner: " + delivery.getDeliveryPartnerId());
        }

        try {
            // Find best available partner
            DeliveryPartner partner = deliveryPartnerService.findBestPartnerForDelivery(
                delivery.getPickupAddress().getLatitude(),
                delivery.getPickupAddress().getLongitude()
            );

            // Assign delivery to partner
            delivery.setDeliveryPartnerId(partner.getPartnerId());
            delivery.setAssignedAt(LocalDateTime.now());
            delivery.setStatus("ASSIGNED");

            // Calculate partner earnings (e.g., 80% of delivery fee)
            if (delivery.getDeliveryFee() != null) {
                delivery.setPartnerEarnings(delivery.getDeliveryFee() * 0.8);
            }

            Delivery updatedDelivery = deliveryRepository.save(delivery);

            // Update partner's assigned orders
            deliveryPartnerService.assignOrderToPartner(partner.getPartnerId(), delivery.getOrderId());

            // Publish assignment event
            publishDeliveryEvent("DELIVERY_ASSIGNED", updatedDelivery);

            logger.info("Delivery {} assigned to partner {}", deliveryId, partner.getPartnerId());
            return updatedDelivery;

        } catch (Exception e) {
            logger.error("Failed to assign delivery partner for delivery: {}", deliveryId, e);
            throw new RuntimeException("Failed to assign delivery partner: " + e.getMessage());
        }
    }

    // Update delivery status
    public Delivery updateDeliveryStatus(String deliveryId, String status) {
        Delivery delivery = getDeliveryById(deliveryId);
        String previousStatus = delivery.getStatus();

        delivery.setStatus(status);

        // Update timestamps based on status
        switch (status) {
            case "PICKED_UP":
                delivery.setPickedUpAt(LocalDateTime.now());
                break;
            case "DELIVERED":
                delivery.setDeliveredAt(LocalDateTime.now());
                // Calculate actual delivery time
                if (delivery.getAssignedAt() != null) {
                    long minutes = ChronoUnit.MINUTES.between(delivery.getAssignedAt(), LocalDateTime.now());
                    delivery.setActualDeliveryTimeMinutes((int) minutes);
                }
                // Complete order for partner
                if (delivery.getDeliveryPartnerId() != null) {
                    deliveryPartnerService.completeOrderForPartner(delivery.getDeliveryPartnerId(), delivery.getOrderId());
                }
                break;
            case "CANCELLED":
                delivery.setCancelledAt(LocalDateTime.now());
                // Remove from partner's assigned orders
                if (delivery.getDeliveryPartnerId() != null) {
                    deliveryPartnerService.completeOrderForPartner(delivery.getDeliveryPartnerId(), delivery.getOrderId());
                }
                break;
        }

        Delivery updatedDelivery = deliveryRepository.save(delivery);

        // Publish status update event
        Map<String, Object> statusEvent = new HashMap<>();
        statusEvent.put("deliveryId", deliveryId);
        statusEvent.put("orderId", delivery.getOrderId());
        statusEvent.put("previousStatus", previousStatus);
        statusEvent.put("newStatus", status);
        statusEvent.put("timestamp", LocalDateTime.now());
        kafkaTemplate.send("delivery-status-updates", statusEvent);

        logger.info("Delivery status updated: {} - {} -> {}", deliveryId, previousStatus, status);
        return updatedDelivery;
    }

    // Update delivery location (from partner)
    public Delivery updateDeliveryLocation(String deliveryId, LocationUpdateRequest request) {
        Delivery delivery = getDeliveryById(deliveryId);

        // Create tracking point
        Delivery.DeliveryTrackingPoint trackingPoint = new Delivery.DeliveryTrackingPoint(
            request.getLatitude(),
            request.getLongitude(),
            request.getStatus() != null ? request.getStatus() : delivery.getStatus()
        );

        List<Delivery.DeliveryTrackingPoint> trackingPoints = delivery.getTrackingPoints();
        if (trackingPoints == null) {
            trackingPoints = new ArrayList<>();
        }
        trackingPoints.add(trackingPoint);
        delivery.setTrackingPoints(trackingPoints);

        // Update current location
        if (delivery.getCurrentLocation() == null) {
            delivery.setCurrentLocation(new com.mergeeats.common.models.Address());
        }
        delivery.getCurrentLocation().setLatitude(request.getLatitude());
        delivery.getCurrentLocation().setLongitude(request.getLongitude());
        delivery.setLastLocationUpdate(LocalDateTime.now());

        Delivery updatedDelivery = deliveryRepository.save(delivery);

        // Publish location update event
        Map<String, Object> locationEvent = new HashMap<>();
        locationEvent.put("deliveryId", deliveryId);
        locationEvent.put("orderId", delivery.getOrderId());
        locationEvent.put("latitude", request.getLatitude());
        locationEvent.put("longitude", request.getLongitude());
        locationEvent.put("status", delivery.getStatus());
        locationEvent.put("timestamp", LocalDateTime.now());
        kafkaTemplate.send("delivery-location-updates", locationEvent);

        return updatedDelivery;
    }

    // Get delivery by ID
    public Delivery getDeliveryById(String deliveryId) {
        return deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new RuntimeException("Delivery not found: " + deliveryId));
    }

    // Get delivery by order ID
    public Delivery getDeliveryByOrderId(String orderId) {
        return deliveryRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Delivery not found for order: " + orderId));
    }

    // Get delivery by tracking code
    public Delivery getDeliveryByTrackingCode(String trackingCode) {
        return deliveryRepository.findByTrackingCode(trackingCode)
            .orElseThrow(() -> new RuntimeException("Delivery not found for tracking code: " + trackingCode));
    }

    // Get deliveries by partner
    public List<Delivery> getDeliveriesByPartner(String partnerId) {
        return deliveryRepository.findByDeliveryPartnerId(partnerId);
    }

    // Get active deliveries by partner
    public List<Delivery> getActiveDeliveriesByPartner(String partnerId) {
        return deliveryRepository.findActiveDeliveriesByPartnerId(partnerId);
    }

    // Get deliveries by customer
    public List<Delivery> getDeliveriesByCustomer(String customerId) {
        return deliveryRepository.findByCustomerId(customerId);
    }

    // Get deliveries by restaurant
    public List<Delivery> getDeliveriesByRestaurant(String restaurantId) {
        return deliveryRepository.findByRestaurantId(restaurantId);
    }

    // Cancel delivery
    public Delivery cancelDelivery(String deliveryId, String reason) {
        Delivery delivery = getDeliveryById(deliveryId);

        if ("DELIVERED".equals(delivery.getStatus())) {
            throw new RuntimeException("Cannot cancel delivered order");
        }

        delivery.setStatus("CANCELLED");
        delivery.setCancelledAt(LocalDateTime.now());
        delivery.setCancellationReason(reason);

        // Remove from partner's assigned orders
        if (delivery.getDeliveryPartnerId() != null) {
            deliveryPartnerService.completeOrderForPartner(delivery.getDeliveryPartnerId(), delivery.getOrderId());
        }

        Delivery updatedDelivery = deliveryRepository.save(delivery);

        // Publish cancellation event
        publishDeliveryEvent("DELIVERY_CANCELLED", updatedDelivery);

        logger.info("Delivery cancelled: {} - {}", deliveryId, reason);
        return updatedDelivery;
    }

    // Rate delivery
    public Delivery rateDelivery(String deliveryId, Double rating, String feedback) {
        Delivery delivery = getDeliveryById(deliveryId);

        if (!"DELIVERED".equals(delivery.getStatus())) {
            throw new RuntimeException("Can only rate delivered orders");
        }

        delivery.setCustomerRating(rating);
        delivery.setCustomerFeedback(feedback);

        Delivery updatedDelivery = deliveryRepository.save(delivery);

        // Update partner rating
        if (delivery.getDeliveryPartnerId() != null) {
            deliveryPartnerService.updatePartnerRating(delivery.getDeliveryPartnerId(), rating);
        }

        // Publish rating event
        publishDeliveryEvent("DELIVERY_RATED", updatedDelivery);

        logger.info("Delivery rated: {} - {} stars", deliveryId, rating);
        return updatedDelivery;
    }

    // Optimize delivery route for partner
    public Map<String, Object> optimizeRouteForPartner(String partnerId) {
        if (!routeOptimizationEnabled) {
            throw new RuntimeException("Route optimization is disabled");
        }

        List<Delivery> deliveries = deliveryRepository.findDeliveriesForRouteOptimization(partnerId);

        if (deliveries.isEmpty()) {
            return Map.of("message", "No deliveries found for route optimization");
        }

        // Simple route optimization using nearest neighbor algorithm
        List<Map<String, Object>> optimizedRoute = optimizeRoute(deliveries);

        Map<String, Object> result = new HashMap<>();
        result.put("partnerId", partnerId);
        result.put("totalDeliveries", deliveries.size());
        result.put("optimizedRoute", optimizedRoute);
        result.put("estimatedTotalDistance", calculateTotalRouteDistance(optimizedRoute));
        result.put("optimizedAt", LocalDateTime.now());

        // Publish route optimization event
        kafkaTemplate.send("route-optimizations", result);

        logger.info("Route optimized for partner: {} - {} deliveries", partnerId, deliveries.size());
        return result;
    }

    // Get delivery statistics
    public Map<String, Object> getDeliveryStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalDeliveries", deliveryRepository.count());
        stats.put("assignedDeliveries", deliveryRepository.countByStatus("ASSIGNED"));
        stats.put("pickedUpDeliveries", deliveryRepository.countByStatus("PICKED_UP"));
        stats.put("inTransitDeliveries", deliveryRepository.countByStatus("IN_TRANSIT"));
        stats.put("deliveredDeliveries", deliveryRepository.countByStatus("DELIVERED"));
        stats.put("cancelledDeliveries", deliveryRepository.countByStatus("CANCELLED"));

        // Calculate average delivery time
        List<Delivery> completedDeliveries = deliveryRepository.findByStatus("DELIVERED");
        if (!completedDeliveries.isEmpty()) {
            double avgTime = completedDeliveries.stream()
                .filter(d -> d.getActualDeliveryTimeMinutes() != null)
                .mapToInt(Delivery::getActualDeliveryTimeMinutes)
                .average()
                .orElse(0.0);
            stats.put("averageDeliveryTimeMinutes", Math.round(avgTime * 100.0) / 100.0);
        }

        // Calculate average rating
        List<Delivery> ratedDeliveries = deliveryRepository.findDeliveriesWithRating();
        if (!ratedDeliveries.isEmpty()) {
            double avgRating = ratedDeliveries.stream()
                .mapToDouble(Delivery::getCustomerRating)
                .average()
                .orElse(0.0);
            stats.put("averageRating", Math.round(avgRating * 100.0) / 100.0);
        }

        stats.put("mergedDeliveries", deliveryRepository.findMergedDeliveries().size());

        return stats;
    }

    // Helper method to generate tracking code
    private String generateTrackingCode() {
        return "TRK" + System.currentTimeMillis() + (int)(Math.random() * 1000);
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

    // Helper method to optimize route using nearest neighbor algorithm
    private List<Map<String, Object>> optimizeRoute(List<Delivery> deliveries) {
        if (deliveries.isEmpty()) {
            return new ArrayList<>();
        }

        List<Delivery> unvisited = new ArrayList<>(deliveries);
        List<Map<String, Object>> route = new ArrayList<>();

        // Start with the first delivery
        Delivery current = unvisited.remove(0);
        route.add(createRoutePoint(current, "pickup"));
        route.add(createRoutePoint(current, "delivery"));

        // Find nearest neighbors
        while (!unvisited.isEmpty() && route.size() < maxWaypoints) {
            Delivery nearest = findNearestDelivery(current, unvisited);
            unvisited.remove(nearest);
            
            route.add(createRoutePoint(nearest, "pickup"));
            route.add(createRoutePoint(nearest, "delivery"));
            
            current = nearest;
        }

        return route;
    }

    // Helper method to find nearest delivery
    private Delivery findNearestDelivery(Delivery current, List<Delivery> candidates) {
        return candidates.stream()
            .min((d1, d2) -> {
                double dist1 = calculateDistance(
                    current.getDeliveryAddress().getLatitude(),
                    current.getDeliveryAddress().getLongitude(),
                    d1.getPickupAddress().getLatitude(),
                    d1.getPickupAddress().getLongitude()
                );
                double dist2 = calculateDistance(
                    current.getDeliveryAddress().getLatitude(),
                    current.getDeliveryAddress().getLongitude(),
                    d2.getPickupAddress().getLatitude(),
                    d2.getPickupAddress().getLongitude()
                );
                return Double.compare(dist1, dist2);
            })
            .orElse(candidates.get(0));
    }

    // Helper method to create route point
    private Map<String, Object> createRoutePoint(Delivery delivery, String type) {
        Map<String, Object> point = new HashMap<>();
        point.put("deliveryId", delivery.getDeliveryId());
        point.put("orderId", delivery.getOrderId());
        point.put("type", type);
        
        if ("pickup".equals(type)) {
            point.put("latitude", delivery.getPickupAddress().getLatitude());
            point.put("longitude", delivery.getPickupAddress().getLongitude());
            point.put("address", delivery.getPickupAddress());
        } else {
            point.put("latitude", delivery.getDeliveryAddress().getLatitude());
            point.put("longitude", delivery.getDeliveryAddress().getLongitude());
            point.put("address", delivery.getDeliveryAddress());
        }
        
        return point;
    }

    // Helper method to calculate total route distance
    private double calculateTotalRouteDistance(List<Map<String, Object>> route) {
        if (route.size() < 2) return 0.0;

        double totalDistance = 0.0;
        for (int i = 1; i < route.size(); i++) {
            Map<String, Object> prev = route.get(i - 1);
            Map<String, Object> curr = route.get(i);
            
            totalDistance += calculateDistance(
                (Double) prev.get("latitude"),
                (Double) prev.get("longitude"),
                (Double) curr.get("latitude"),
                (Double) curr.get("longitude")
            );
        }
        
        return Math.round(totalDistance * 100.0) / 100.0;
    }

    // Helper method to publish delivery events
    private void publishDeliveryEvent(String eventType, Delivery delivery) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", eventType);
        event.put("deliveryId", delivery.getDeliveryId());
        event.put("orderId", delivery.getOrderId());
        event.put("customerId", delivery.getCustomerId());
        event.put("restaurantId", delivery.getRestaurantId());
        event.put("deliveryPartnerId", delivery.getDeliveryPartnerId());
        event.put("status", delivery.getStatus());
        event.put("timestamp", LocalDateTime.now());

        kafkaTemplate.send("delivery-events", event);
    }
}