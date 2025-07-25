package com.mergeeats.deliveryservice.service;

import com.mergeeats.common.models.Delivery;
import com.mergeeats.common.models.DeliveryPartner;
import com.mergeeats.common.models.Address;
import com.mergeeats.common.enums.DeliveryStatus;
import com.mergeeats.common.enums.DeliveryPartnerStatus;
import com.mergeeats.deliveryservice.repository.DeliveryRepository;
import com.mergeeats.deliveryservice.repository.DeliveryPartnerRepository;
import com.mergeeats.deliveryservice.dto.CreateDeliveryRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${delivery.assignment.max-distance-km:10.0}")
    private Double maxAssignmentDistance;

    @Value("${delivery.assignment.max-orders-per-partner:5}")
    private Integer maxOrdersPerPartner;

    @Value("${delivery.assignment.auto-assignment-enabled:true}")
    private Boolean autoAssignmentEnabled;

    // Create delivery
    public Delivery createDelivery(CreateDeliveryRequest request) {
        Delivery delivery = new Delivery();
        delivery.setOrderId(request.getOrderId());
        delivery.setCustomerId(request.getCustomerId());
        delivery.setRestaurantId(request.getRestaurantId());
        delivery.setPickupAddress(request.getPickupAddress());
        delivery.setDeliveryAddress(request.getDeliveryAddress());
        delivery.setDeliveryFee(request.getDeliveryFee());
        delivery.setSpecialInstructions(request.getSpecialInstructions());
        delivery.setCustomerPhoneNumber(request.getCustomerPhoneNumber());
        delivery.setRestaurantPhoneNumber(request.getRestaurantPhoneNumber());
        delivery.setScheduledPickupTime(request.getScheduledPickupTime());
        delivery.setEstimatedDeliveryTime(request.getEstimatedDeliveryTime());

        // Calculate distance and estimated time
        Double distance = calculateDistance(request.getPickupAddress(), request.getDeliveryAddress());
        delivery.setDistanceKm(distance);
        
        if (delivery.getEstimatedTimeMinutes() == null) {
            delivery.setEstimatedTimeMinutes(calculateEstimatedTime(distance));
        }

        delivery = deliveryRepository.save(delivery);

        // Auto-assign if enabled
        if (autoAssignmentEnabled) {
            try {
                assignDeliveryPartner(delivery.getDeliveryId());
            } catch (Exception e) {
                // Log error but don't fail delivery creation
                System.err.println("Auto-assignment failed for delivery " + delivery.getDeliveryId() + ": " + e.getMessage());
            }
        }

        // Publish event
        publishDeliveryEvent("delivery.created", delivery);

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

    // Assign delivery partner
    public Delivery assignDeliveryPartner(String deliveryId) {
        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        if (deliveryOpt.isEmpty()) {
            throw new RuntimeException("Delivery not found: " + deliveryId);
        }

        Delivery delivery = deliveryOpt.get();
        if (delivery.getStatus() != DeliveryStatus.PENDING) {
            throw new RuntimeException("Delivery is not in PENDING status");
        }

        // Find suitable delivery partner
        DeliveryPartner partner = findBestDeliveryPartner(delivery);
        if (partner == null) {
            throw new RuntimeException("No available delivery partner found");
        }

        // Assign partner
        delivery.setDeliveryPartnerId(partner.getPartnerId());
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now());

        // Calculate partner earnings (example: 70% of delivery fee)
        if (delivery.getDeliveryFee() != null) {
            delivery.setPartnerEarnings(delivery.getDeliveryFee() * 0.7);
        }

        delivery = deliveryRepository.save(delivery);

        // Update partner status
        partner.setStatus(DeliveryPartnerStatus.BUSY);
        deliveryPartnerRepository.save(partner);

        // Publish events
        publishDeliveryEvent("delivery.assigned", delivery);
        publishPartnerEvent("partner.assigned", partner, delivery);

        return delivery;
    }

    // Update delivery status
    public Delivery updateDeliveryStatus(String deliveryId, DeliveryStatus newStatus) {
        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        if (deliveryOpt.isEmpty()) {
            throw new RuntimeException("Delivery not found: " + deliveryId);
        }

        Delivery delivery = deliveryOpt.get();
        DeliveryStatus oldStatus = delivery.getStatus();

        // Validate status transition
        if (!oldStatus.canTransitionTo(newStatus)) {
            throw new RuntimeException("Invalid status transition from " + oldStatus + " to " + newStatus);
        }

        // Update status and timestamps
        delivery.setStatus(newStatus);
        LocalDateTime now = LocalDateTime.now();

        switch (newStatus) {
            case ACCEPTED:
                delivery.setAcceptedAt(now);
                break;
            case PICKED_UP:
                delivery.setPickedUpAt(now);
                delivery.setActualPickupTime(now);
                break;
            case IN_TRANSIT:
                // Status is set when partner starts delivery
                break;
            case DELIVERED:
                delivery.setDeliveredAt(now);
                delivery.setActualDeliveryTime(now);
                updatePartnerStats(delivery);
                break;
            case CANCELLED:
            case FAILED:
                delivery.setCancelledAt(now);
                break;
        }

        delivery = deliveryRepository.save(delivery);

        // Update partner availability if delivery is completed
        if (newStatus.isCompleted() || newStatus.isCancelled()) {
            updatePartnerAvailability(delivery.getDeliveryPartnerId());
        }

        // Publish event
        publishDeliveryEvent("delivery.status.updated", delivery);

        return delivery;
    }

    // Update delivery location
    public Delivery updateDeliveryLocation(String deliveryId, Address currentLocation) {
        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        if (deliveryOpt.isEmpty()) {
            throw new RuntimeException("Delivery not found: " + deliveryId);
        }

        Delivery delivery = deliveryOpt.get();
        delivery.setCurrentLocation(currentLocation);
        delivery.setLastLocationUpdate(LocalDateTime.now());

        delivery = deliveryRepository.save(delivery);

        // Publish real-time location update
        publishLocationUpdate(delivery);

        return delivery;
    }

    // Get deliveries by partner
    public List<Delivery> getDeliveriesByPartnerId(String partnerId) {
        return deliveryRepository.findByDeliveryPartnerId(partnerId);
    }

    // Get active deliveries by partner
    public List<Delivery> getActiveDeliveriesByPartnerId(String partnerId) {
        return deliveryRepository.findActiveDeliveriesByPartnerId(partnerId);
    }

    // Get deliveries by customer
    public List<Delivery> getDeliveriesByCustomerId(String customerId) {
        return deliveryRepository.findByCustomerId(customerId);
    }

    // Get deliveries by restaurant
    public List<Delivery> getDeliveriesByRestaurantId(String restaurantId) {
        return deliveryRepository.findByRestaurantId(restaurantId);
    }

    // Get deliveries by status
    public List<Delivery> getDeliveriesByStatus(DeliveryStatus status) {
        return deliveryRepository.findByStatus(status);
    }

    // Cancel delivery
    public Delivery cancelDelivery(String deliveryId, String reason) {
        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        if (deliveryOpt.isEmpty()) {
            throw new RuntimeException("Delivery not found: " + deliveryId);
        }

        Delivery delivery = deliveryOpt.get();
        if (delivery.getStatus().isCompleted()) {
            throw new RuntimeException("Cannot cancel completed delivery");
        }

        delivery.setStatus(DeliveryStatus.CANCELLED);
        delivery.setCancelledAt(LocalDateTime.now());
        delivery.setCancellationReason(reason);

        delivery = deliveryRepository.save(delivery);

        // Update partner availability
        if (delivery.getDeliveryPartnerId() != null) {
            updatePartnerAvailability(delivery.getDeliveryPartnerId());
        }

        // Publish event
        publishDeliveryEvent("delivery.cancelled", delivery);

        return delivery;
    }

    // Find best delivery partner
    private DeliveryPartner findBestDeliveryPartner(Delivery delivery) {
        Address pickupLocation = delivery.getPickupAddress();
        if (pickupLocation.getLatitude() == null || pickupLocation.getLongitude() == null) {
            return null;
        }

        // Calculate search area
        double[] bounds = calculateSearchBounds(pickupLocation.getLatitude(), pickupLocation.getLongitude(), maxAssignmentDistance);

        // Find available partners in area
        List<DeliveryPartner> availablePartners = deliveryPartnerRepository.findPartnersForAssignment(
            bounds[0], bounds[1], bounds[2], bounds[3]
        );

        if (availablePartners.isEmpty()) {
            return null;
        }

        // Filter partners by capacity
        availablePartners = availablePartners.stream()
            .filter(partner -> {
                long activeDeliveries = deliveryRepository.countByDeliveryPartnerIdAndStatus(
                    partner.getPartnerId(), DeliveryStatus.ASSIGNED
                );
                return activeDeliveries < maxOrdersPerPartner;
            })
            .collect(Collectors.toList());

        if (availablePartners.isEmpty()) {
            return null;
        }

        // Find best partner based on distance and rating
        return availablePartners.stream()
            .min((p1, p2) -> {
                double distance1 = calculateDistance(pickupLocation, p1.getCurrentLocation());
                double distance2 = calculateDistance(pickupLocation, p2.getCurrentLocation());
                
                // Weighted score: 70% distance, 30% rating
                double score1 = distance1 * 0.7 - (p1.getRating() * 0.3);
                double score2 = distance2 * 0.7 - (p2.getRating() * 0.3);
                
                return Double.compare(score1, score2);
            })
            .orElse(null);
    }

    // Calculate distance between two addresses
    private Double calculateDistance(Address addr1, Address addr2) {
        if (addr1 == null || addr2 == null || 
            addr1.getLatitude() == null || addr1.getLongitude() == null ||
            addr2.getLatitude() == null || addr2.getLongitude() == null) {
            return 0.0;
        }

        // Haversine formula
        double lat1Rad = Math.toRadians(addr1.getLatitude());
        double lat2Rad = Math.toRadians(addr2.getLatitude());
        double deltaLatRad = Math.toRadians(addr2.getLatitude() - addr1.getLatitude());
        double deltaLngRad = Math.toRadians(addr2.getLongitude() - addr1.getLongitude());

        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLngRad / 2) * Math.sin(deltaLngRad / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371.0 * c; // Earth's radius in kilometers
    }

    // Calculate estimated delivery time
    private Integer calculateEstimatedTime(Double distanceKm) {
        if (distanceKm == null || distanceKm <= 0) {
            return 30; // Default 30 minutes
        }

        // Estimate: 25 km/h average speed + 10 minutes preparation/pickup time
        return (int) Math.ceil((distanceKm / 25.0) * 60) + 10;
    }

    // Calculate search bounds for partner lookup
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

    // Update partner stats after successful delivery
    private void updatePartnerStats(Delivery delivery) {
        if (delivery.getDeliveryPartnerId() == null) return;

        Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(delivery.getDeliveryPartnerId());
        if (partnerOpt.isPresent()) {
            DeliveryPartner partner = partnerOpt.get();
            partner.setTotalDeliveries(partner.getTotalDeliveries() + 1);
            
            if (delivery.getPartnerEarnings() != null) {
                partner.setTotalEarnings(partner.getTotalEarnings() + delivery.getPartnerEarnings());
            }
            
            partner.setLastActiveTime(LocalDateTime.now());
            deliveryPartnerRepository.save(partner);
        }
    }

    // Update partner availability after delivery completion
    private void updatePartnerAvailability(String partnerId) {
        if (partnerId == null) return;

        Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
        if (partnerOpt.isPresent()) {
            DeliveryPartner partner = partnerOpt.get();
            
            // Check if partner has other active deliveries
            long activeDeliveries = deliveryRepository.findActiveDeliveriesByPartnerId(partnerId).size();
            
            if (activeDeliveries == 0) {
                partner.setStatus(DeliveryPartnerStatus.ONLINE);
                deliveryPartnerRepository.save(partner);
            }
        }
    }

    // Publish delivery event to Kafka
    private void publishDeliveryEvent(String eventType, Delivery delivery) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventType);
            event.put("deliveryId", delivery.getDeliveryId());
            event.put("orderId", delivery.getOrderId());
            event.put("customerId", delivery.getCustomerId());
            event.put("restaurantId", delivery.getRestaurantId());
            event.put("deliveryPartnerId", delivery.getDeliveryPartnerId());
            event.put("status", delivery.getStatus().name());
            event.put("timestamp", LocalDateTime.now());

            kafkaTemplate.send("delivery-events", event);
        } catch (Exception e) {
            System.err.println("Failed to publish delivery event: " + e.getMessage());
        }
    }

    // Publish partner event to Kafka
    private void publishPartnerEvent(String eventType, DeliveryPartner partner, Delivery delivery) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventType);
            event.put("partnerId", partner.getPartnerId());
            event.put("deliveryId", delivery.getDeliveryId());
            event.put("orderId", delivery.getOrderId());
            event.put("timestamp", LocalDateTime.now());

            kafkaTemplate.send("delivery-partner-events", event);
        } catch (Exception e) {
            System.err.println("Failed to publish partner event: " + e.getMessage());
        }
    }

    // Publish real-time location update
    private void publishLocationUpdate(Delivery delivery) {
        try {
            Map<String, Object> locationUpdate = new HashMap<>();
            locationUpdate.put("deliveryId", delivery.getDeliveryId());
            locationUpdate.put("orderId", delivery.getOrderId());
            locationUpdate.put("currentLocation", delivery.getCurrentLocation());
            locationUpdate.put("timestamp", delivery.getLastLocationUpdate());

            kafkaTemplate.send("delivery-location-updates", locationUpdate);
        } catch (Exception e) {
            System.err.println("Failed to publish location update: " + e.getMessage());
        }
    }
}