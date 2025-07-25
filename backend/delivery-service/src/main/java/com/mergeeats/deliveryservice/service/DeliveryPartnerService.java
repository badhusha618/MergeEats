package com.mergeeats.deliveryservice.service;

import com.mergeeats.common.models.DeliveryPartner;
import com.mergeeats.common.models.DeliveryPartner.AvailabilityStatus;
import com.mergeeats.common.models.DeliveryPartner.VehicleType;
import com.mergeeats.common.models.Address;
import com.mergeeats.deliveryservice.repository.DeliveryPartnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class DeliveryPartnerService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryPartnerService.class);

    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // Registration and Profile Management
    public DeliveryPartner registerPartner(DeliveryPartner partner) {
        try {
            // Check for existing partner
            if (deliveryPartnerRepository.findByUserId(partner.getUserId()).isPresent()) {
                throw new RuntimeException("Delivery partner already exists for this user");
            }
            
            if (deliveryPartnerRepository.findByEmail(partner.getEmail()).isPresent()) {
                throw new RuntimeException("Email already registered");
            }
            
            if (deliveryPartnerRepository.findByPhoneNumber(partner.getPhoneNumber()).isPresent()) {
                throw new RuntimeException("Phone number already registered");
            }

            // Set default values
            partner.setAvailabilityStatus(AvailabilityStatus.OFFLINE);
            partner.setIsActive(true);
            partner.setIsVerified(false);
            partner.setRating(0.0);
            partner.setTotalDeliveries(0);
            partner.setCompletedDeliveries(0);
            partner.setCancelledDeliveries(0);
            partner.setTotalEarnings(0.0);
            partner.setCurrentBalance(0.0);
            partner.setActiveOrderIds(new ArrayList<>());
            partner.setLastActiveTime(LocalDateTime.now());

            DeliveryPartner savedPartner = deliveryPartnerRepository.save(partner);
            
            // Publish event
            publishPartnerEvent("delivery-partner-registered", savedPartner);
            
            logger.info("Delivery partner registered successfully: {}", savedPartner.getPartnerId());
            return savedPartner;
            
        } catch (Exception e) {
            logger.error("Error registering delivery partner: {}", e.getMessage());
            throw e;
        }
    }

    public Optional<DeliveryPartner> getPartnerById(String partnerId) {
        return deliveryPartnerRepository.findById(partnerId);
    }

    public Optional<DeliveryPartner> getPartnerByUserId(String userId) {
        return deliveryPartnerRepository.findByUserId(userId);
    }

    public DeliveryPartner updatePartner(String partnerId, DeliveryPartner updatedPartner) {
        try {
            Optional<DeliveryPartner> existingPartner = deliveryPartnerRepository.findById(partnerId);
            if (existingPartner.isEmpty()) {
                throw new RuntimeException("Delivery partner not found");
            }

            DeliveryPartner partner = existingPartner.get();
            
            // Update allowed fields
            if (updatedPartner.getFullName() != null) {
                partner.setFullName(updatedPartner.getFullName());
            }
            if (updatedPartner.getPhoneNumber() != null) {
                partner.setPhoneNumber(updatedPartner.getPhoneNumber());
            }
            if (updatedPartner.getVehicleType() != null) {
                partner.setVehicleType(updatedPartner.getVehicleType());
            }
            if (updatedPartner.getVehicleRegistrationNumber() != null) {
                partner.setVehicleRegistrationNumber(updatedPartner.getVehicleRegistrationNumber());
            }
            if (updatedPartner.getLicenseNumber() != null) {
                partner.setLicenseNumber(updatedPartner.getLicenseNumber());
            }
            if (updatedPartner.getMaxConcurrentOrders() != null) {
                partner.setMaxConcurrentOrders(updatedPartner.getMaxConcurrentOrders());
            }
            if (updatedPartner.getDeliveryRadius() != null) {
                partner.setDeliveryRadius(updatedPartner.getDeliveryRadius());
            }

            DeliveryPartner savedPartner = deliveryPartnerRepository.save(partner);
            publishPartnerEvent("delivery-partner-updated", savedPartner);
            
            logger.info("Delivery partner updated successfully: {}", partnerId);
            return savedPartner;
            
        } catch (Exception e) {
            logger.error("Error updating delivery partner {}: {}", partnerId, e.getMessage());
            throw e;
        }
    }

    // Availability Management
    public DeliveryPartner updateAvailabilityStatus(String partnerId, AvailabilityStatus status) {
        try {
            Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
            if (partnerOpt.isEmpty()) {
                throw new RuntimeException("Delivery partner not found");
            }

            DeliveryPartner partner = partnerOpt.get();
            AvailabilityStatus oldStatus = partner.getAvailabilityStatus();
            partner.setAvailabilityStatus(status);
            partner.setLastActiveTime(LocalDateTime.now());

            DeliveryPartner savedPartner = deliveryPartnerRepository.save(partner);

            // Publish status change event
            publishPartnerStatusChangeEvent(savedPartner, oldStatus, status);
            
            logger.info("Partner {} availability status changed from {} to {}", partnerId, oldStatus, status);
            return savedPartner;
            
        } catch (Exception e) {
            logger.error("Error updating partner availability status: {}", e.getMessage());
            throw e;
        }
    }

    public DeliveryPartner updateLocation(String partnerId, Address location) {
        try {
            Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
            if (partnerOpt.isEmpty()) {
                throw new RuntimeException("Delivery partner not found");
            }

            DeliveryPartner partner = partnerOpt.get();
            partner.setCurrentLocation(location);
            partner.setLastActiveTime(LocalDateTime.now());

            DeliveryPartner savedPartner = deliveryPartnerRepository.save(partner);
            
            // Publish location update event
            publishPartnerLocationUpdateEvent(savedPartner);
            
            return savedPartner;
            
        } catch (Exception e) {
            logger.error("Error updating partner location: {}", e.getMessage());
            throw e;
        }
    }

    // Partner Discovery
    public List<DeliveryPartner> getAvailablePartners() {
        return deliveryPartnerRepository.findByAvailabilityStatusAndIsActiveTrueAndIsVerifiedTrue(AvailabilityStatus.AVAILABLE);
    }

    public List<DeliveryPartner> getPartnersInArea(double latitude, double longitude, double radiusKm) {
        // Calculate bounding box for the area
        double latRange = radiusKm / 111.0; // Approximate km per degree latitude
        double lonRange = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude))); // Adjust for longitude

        double minLat = latitude - latRange;
        double maxLat = latitude + latRange;
        double minLon = longitude - lonRange;
        double maxLon = longitude + lonRange;

        return deliveryPartnerRepository.findAvailablePartnersInArea(minLat, maxLat, minLon, maxLon, AvailabilityStatus.AVAILABLE);
    }

    public List<DeliveryPartner> getOptimalPartnersForOrder(double latitude, double longitude, double radiusKm, double minRating) {
        // Calculate bounding box
        double latRange = radiusKm / 111.0;
        double lonRange = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)));

        double minLat = latitude - latRange;
        double maxLat = latitude + latRange;
        double minLon = longitude - lonRange;
        double maxLon = longitude + lonRange;

        List<DeliveryPartner> partners = deliveryPartnerRepository.findOptimalPartnersInArea(minLat, maxLat, minLon, maxLon, minRating);
        
        // Sort by rating (descending) and then by distance (ascending)
        return partners.stream()
                .filter(partner -> partner.canTakeMoreOrders())
                .sorted((p1, p2) -> {
                    // First sort by rating (higher is better)
                    int ratingCompare = Double.compare(p2.getRating(), p1.getRating());
                    if (ratingCompare != 0) return ratingCompare;
                    
                    // Then by number of active orders (fewer is better)
                    int activeOrdersCompare = Integer.compare(
                        p1.getActiveOrderIds() != null ? p1.getActiveOrderIds().size() : 0,
                        p2.getActiveOrderIds() != null ? p2.getActiveOrderIds().size() : 0
                    );
                    if (activeOrdersCompare != 0) return activeOrdersCompare;
                    
                    // Finally by completion rate (higher is better)
                    return Double.compare(p2.getCompletionRate(), p1.getCompletionRate());
                })
                .collect(Collectors.toList());
    }

    // Order Management
    public DeliveryPartner assignOrder(String partnerId, String orderId) {
        try {
            Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
            if (partnerOpt.isEmpty()) {
                throw new RuntimeException("Delivery partner not found");
            }

            DeliveryPartner partner = partnerOpt.get();
            
            if (!partner.isAvailable()) {
                throw new RuntimeException("Partner is not available for assignment");
            }
            
            if (!partner.canTakeMoreOrders()) {
                throw new RuntimeException("Partner has reached maximum concurrent orders");
            }

            // Add order to active orders
            if (partner.getActiveOrderIds() == null) {
                partner.setActiveOrderIds(new ArrayList<>());
            }
            partner.getActiveOrderIds().add(orderId);
            
            // Update status if this is the first order
            if (partner.getActiveOrderIds().size() == 1) {
                partner.setAvailabilityStatus(AvailabilityStatus.BUSY);
            }

            DeliveryPartner savedPartner = deliveryPartnerRepository.save(partner);
            
            // Publish order assignment event
            publishOrderAssignmentEvent(savedPartner, orderId);
            
            logger.info("Order {} assigned to partner {}", orderId, partnerId);
            return savedPartner;
            
        } catch (Exception e) {
            logger.error("Error assigning order {} to partner {}: {}", orderId, partnerId, e.getMessage());
            throw e;
        }
    }

    public DeliveryPartner completeOrder(String partnerId, String orderId) {
        try {
            Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
            if (partnerOpt.isEmpty()) {
                throw new RuntimeException("Delivery partner not found");
            }

            DeliveryPartner partner = partnerOpt.get();
            
            if (partner.getActiveOrderIds() == null || !partner.getActiveOrderIds().contains(orderId)) {
                throw new RuntimeException("Order not found in partner's active orders");
            }

            // Remove order from active orders
            partner.getActiveOrderIds().remove(orderId);
            
            // Update statistics
            partner.setCompletedDeliveries(partner.getCompletedDeliveries() + 1);
            partner.setTotalDeliveries(partner.getTotalDeliveries() + 1);
            
            // Update availability status if no more active orders
            if (partner.getActiveOrderIds().isEmpty()) {
                partner.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            }

            DeliveryPartner savedPartner = deliveryPartnerRepository.save(partner);
            
            // Publish order completion event
            publishOrderCompletionEvent(savedPartner, orderId);
            
            logger.info("Order {} completed by partner {}", orderId, partnerId);
            return savedPartner;
            
        } catch (Exception e) {
            logger.error("Error completing order {} for partner {}: {}", orderId, partnerId, e.getMessage());
            throw e;
        }
    }

    public DeliveryPartner cancelOrder(String partnerId, String orderId, String reason) {
        try {
            Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
            if (partnerOpt.isEmpty()) {
                throw new RuntimeException("Delivery partner not found");
            }

            DeliveryPartner partner = partnerOpt.get();
            
            if (partner.getActiveOrderIds() == null || !partner.getActiveOrderIds().contains(orderId)) {
                throw new RuntimeException("Order not found in partner's active orders");
            }

            // Remove order from active orders
            partner.getActiveOrderIds().remove(orderId);
            
            // Update statistics
            partner.setCancelledDeliveries(partner.getCancelledDeliveries() + 1);
            partner.setTotalDeliveries(partner.getTotalDeliveries() + 1);
            
            // Update availability status if no more active orders
            if (partner.getActiveOrderIds().isEmpty()) {
                partner.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            }

            DeliveryPartner savedPartner = deliveryPartnerRepository.save(partner);
            
            // Publish order cancellation event
            publishOrderCancellationEvent(savedPartner, orderId, reason);
            
            logger.info("Order {} cancelled by partner {} - Reason: {}", orderId, partnerId, reason);
            return savedPartner;
            
        } catch (Exception e) {
            logger.error("Error cancelling order {} for partner {}: {}", orderId, partnerId, e.getMessage());
            throw e;
        }
    }

    // Rating and Performance
    public DeliveryPartner updateRating(String partnerId, double newRating, int totalRatings) {
        try {
            Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
            if (partnerOpt.isEmpty()) {
                throw new RuntimeException("Delivery partner not found");
            }

            DeliveryPartner partner = partnerOpt.get();
            
            // Calculate weighted average rating
            double currentRating = partner.getRating();
            int currentDeliveries = partner.getCompletedDeliveries();
            
            if (currentDeliveries > 0) {
                double totalScore = (currentRating * currentDeliveries) + newRating;
                partner.setRating(totalScore / (currentDeliveries + 1));
            } else {
                partner.setRating(newRating);
            }

            DeliveryPartner savedPartner = deliveryPartnerRepository.save(partner);
            
            // Publish rating update event
            publishPartnerRatingUpdateEvent(savedPartner, newRating);
            
            logger.info("Rating updated for partner {}: {}", partnerId, partner.getRating());
            return savedPartner;
            
        } catch (Exception e) {
            logger.error("Error updating rating for partner {}: {}", partnerId, e.getMessage());
            throw e;
        }
    }

    // Verification
    public DeliveryPartner verifyPartner(String partnerId) {
        try {
            Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
            if (partnerOpt.isEmpty()) {
                throw new RuntimeException("Delivery partner not found");
            }

            DeliveryPartner partner = partnerOpt.get();
            partner.setIsVerified(true);

            DeliveryPartner savedPartner = deliveryPartnerRepository.save(partner);
            
            // Publish verification event
            publishPartnerEvent("delivery-partner-verified", savedPartner);
            
            logger.info("Delivery partner verified: {}", partnerId);
            return savedPartner;
            
        } catch (Exception e) {
            logger.error("Error verifying partner {}: {}", partnerId, e.getMessage());
            throw e;
        }
    }

    public DeliveryPartner deactivatePartner(String partnerId, String reason) {
        try {
            Optional<DeliveryPartner> partnerOpt = deliveryPartnerRepository.findById(partnerId);
            if (partnerOpt.isEmpty()) {
                throw new RuntimeException("Delivery partner not found");
            }

            DeliveryPartner partner = partnerOpt.get();
            partner.setIsActive(false);
            partner.setAvailabilityStatus(AvailabilityStatus.OFFLINE);

            DeliveryPartner savedPartner = deliveryPartnerRepository.save(partner);
            
            // Publish deactivation event
            publishPartnerDeactivationEvent(savedPartner, reason);
            
            logger.info("Delivery partner deactivated: {} - Reason: {}", partnerId, reason);
            return savedPartner;
            
        } catch (Exception e) {
            logger.error("Error deactivating partner {}: {}", partnerId, e.getMessage());
            throw e;
        }
    }

    // Statistics and Analytics
    public List<DeliveryPartner> getTopPerformingPartners(int limit) {
        return deliveryPartnerRepository.findByIsActiveTrueAndIsVerifiedTrue()
                .stream()
                .filter(partner -> partner.getCompletedDeliveries() > 10) // Minimum deliveries
                .sorted((p1, p2) -> {
                    // Sort by rating first
                    int ratingCompare = Double.compare(p2.getRating(), p1.getRating());
                    if (ratingCompare != 0) return ratingCompare;
                    
                    // Then by completion rate
                    return Double.compare(p2.getCompletionRate(), p1.getCompletionRate());
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<DeliveryPartner> getPartnersByVehicleType(VehicleType vehicleType) {
        return deliveryPartnerRepository.findByVehicleTypeAndAvailabilityStatusAndIsActiveTrueAndIsVerifiedTrue(
            vehicleType, AvailabilityStatus.AVAILABLE);
    }

    // Event Publishing
    private void publishPartnerEvent(String eventType, DeliveryPartner partner) {
        try {
            kafkaTemplate.send("delivery-partner-events", eventType, partner);
        } catch (Exception e) {
            logger.error("Error publishing partner event: {}", e.getMessage());
        }
    }

    private void publishPartnerStatusChangeEvent(DeliveryPartner partner, AvailabilityStatus oldStatus, AvailabilityStatus newStatus) {
        try {
            var event = new Object() {
                public final String partnerId = partner.getPartnerId();
                public final String userId = partner.getUserId();
                public final AvailabilityStatus previousStatus = oldStatus;
                public final AvailabilityStatus currentStatus = newStatus;
                public final LocalDateTime timestamp = LocalDateTime.now();
            };
            kafkaTemplate.send("delivery-partner-status-updates", "status-changed", event);
        } catch (Exception e) {
            logger.error("Error publishing partner status change event: {}", e.getMessage());
        }
    }

    private void publishPartnerLocationUpdateEvent(DeliveryPartner partner) {
        try {
            var event = new Object() {
                public final String partnerId = partner.getPartnerId();
                public final String userId = partner.getUserId();
                public final Address location = partner.getCurrentLocation();
                public final LocalDateTime timestamp = LocalDateTime.now();
            };
            kafkaTemplate.send("delivery-partner-location-updates", "location-updated", event);
        } catch (Exception e) {
            logger.error("Error publishing partner location update event: {}", e.getMessage());
        }
    }

    private void publishOrderAssignmentEvent(DeliveryPartner partner, String orderId) {
        try {
            var event = new Object() {
                public final String partnerId = partner.getPartnerId();
                public final String assignedOrderId = orderId;
                public final String userId = partner.getUserId();
                public final LocalDateTime timestamp = LocalDateTime.now();
            };
            kafkaTemplate.send("delivery-assignment-events", "order-assigned", event);
        } catch (Exception e) {
            logger.error("Error publishing order assignment event: {}", e.getMessage());
        }
    }

    private void publishOrderCompletionEvent(DeliveryPartner partner, String orderId) {
        try {
            var event = new Object() {
                public final String partnerId = partner.getPartnerId();
                public final String completedOrderId = orderId;
                public final String userId = partner.getUserId();
                public final LocalDateTime timestamp = LocalDateTime.now();
            };
            kafkaTemplate.send("delivery-completion-events", "order-completed", event);
        } catch (Exception e) {
            logger.error("Error publishing order completion event: {}", e.getMessage());
        }
    }

    private void publishOrderCancellationEvent(DeliveryPartner partner, String orderId, String reason) {
        try {
            var event = new Object() {
                public final String partnerId = partner.getPartnerId();
                public final String cancelledOrderId = orderId;
                public final String userId = partner.getUserId();
                public final String cancellationReason = reason;
                public final LocalDateTime timestamp = LocalDateTime.now();
            };
            kafkaTemplate.send("delivery-cancellation-events", "order-cancelled", event);
        } catch (Exception e) {
            logger.error("Error publishing order cancellation event: {}", e.getMessage());
        }
    }

    private void publishPartnerRatingUpdateEvent(DeliveryPartner partner, double newRating) {
        try {
            var event = new Object() {
                public final String partnerId = partner.getPartnerId();
                public final String userId = partner.getUserId();
                public final double updatedRating = newRating;
                public final double currentRating = partner.getRating();
                public final LocalDateTime timestamp = LocalDateTime.now();
            };
            kafkaTemplate.send("delivery-partner-rating-updates", "rating-updated", event);
        } catch (Exception e) {
            logger.error("Error publishing partner rating update event: {}", e.getMessage());
        }
    }

    private void publishPartnerDeactivationEvent(DeliveryPartner partner, String reason) {
        try {
            var event = new Object() {
                public final String partnerId = partner.getPartnerId();
                public final String userId = partner.getUserId();
                public final String deactivationReason = reason;
                public final LocalDateTime timestamp = LocalDateTime.now();
            };
            kafkaTemplate.send("delivery-partner-events", "partner-deactivated", event);
        } catch (Exception e) {
            logger.error("Error publishing partner deactivation event: {}", e.getMessage());
        }
    }
}