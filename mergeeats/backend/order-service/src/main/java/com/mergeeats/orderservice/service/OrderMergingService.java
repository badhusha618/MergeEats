package com.mergeeats.orderservice.service;

import com.mergeeats.common.models.Order;
import com.mergeeats.common.models.Address;
import com.mergeeats.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderMergingService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${order.merge.max-distance-km:2.0}")
    private double maxDistanceKm;
    
    @Value("${order.merge.max-orders-per-merge:5}")
    private int maxOrdersPerMerge;
    
    /**
     * AI-powered order merging algorithm
     * Considers factors like location proximity, delivery time windows, and restaurant preparation time
     */
    public List<Order> mergeOrders(List<Order> candidateOrders) {
        if (candidateOrders.size() < 2) {
            return candidateOrders;
        }
        
        // Step 1: Group orders by optimal clusters based on delivery addresses
        List<List<Order>> clusters = clusterOrdersByLocation(candidateOrders);
        
        List<Order> mergedOrders = new ArrayList<>();
        
        for (List<Order> cluster : clusters) {
            if (cluster.size() > 1 && cluster.size() <= maxOrdersPerMerge) {
                // Step 2: Calculate merge efficiency score
                double efficiency = calculateMergeEfficiency(cluster);
                
                // Step 3: Only merge if efficiency threshold is met
                if (efficiency > 0.7) { // 70% efficiency threshold
                    String mergedOrderId = UUID.randomUUID().toString();
                    
                    // Update orders with merge information
                    for (Order order : cluster) {
                        order.setMerged(true);
                        order.setMergedOrderId(mergedOrderId);
                        order.setMergedWithOrderIds(
                            cluster.stream()
                                .map(Order::getOrderId)
                                .filter(id -> !id.equals(order.getOrderId()))
                                .collect(Collectors.toList())
                        );
                        
                        // Calculate estimated delivery time based on merged route
                        order.setEstimatedDeliveryTime(calculateMergedDeliveryTime(cluster));
                        
                        mergedOrders.add(orderRepository.save(order));
                    }
                    
                    // Publish merge event
                    publishMergeEvent(mergedOrderId, cluster);
                }
            }
        }
        
        return mergedOrders.isEmpty() ? candidateOrders : mergedOrders;
    }
    
    /**
     * Check if two orders are within delivery radius for merging
     */
    public boolean isWithinDeliveryRadius(Order order1, Order order2) {
        if (order1.getDeliveryAddress() == null || order2.getDeliveryAddress() == null) {
            return false;
        }
        
        double distance = calculateDistance(order1.getDeliveryAddress(), order2.getDeliveryAddress());
        return distance <= maxDistanceKm;
    }
    
    /**
     * Cluster orders by location using a simple distance-based algorithm
     * In a production system, this could use more sophisticated ML clustering algorithms
     */
    private List<List<Order>> clusterOrdersByLocation(List<Order> orders) {
        List<List<Order>> clusters = new ArrayList<>();
        List<Order> unprocessed = new ArrayList<>(orders);
        
        while (!unprocessed.isEmpty()) {
            Order seed = unprocessed.remove(0);
            List<Order> cluster = new ArrayList<>();
            cluster.add(seed);
            
            // Find all orders within radius of the seed order
            Iterator<Order> iterator = unprocessed.iterator();
            while (iterator.hasNext()) {
                Order candidate = iterator.next();
                if (isWithinDeliveryRadius(seed, candidate)) {
                    cluster.add(candidate);
                    iterator.remove();
                }
            }
            
            clusters.add(cluster);
        }
        
        return clusters;
    }
    
    /**
     * Calculate merge efficiency based on multiple factors:
     * - Distance savings
     * - Time window compatibility
     * - Restaurant preparation time alignment
     */
    private double calculateMergeEfficiency(List<Order> orders) {
        if (orders.size() < 2) return 0.0;
        
        // Factor 1: Distance efficiency (0-1 score)
        double distanceEfficiency = calculateDistanceEfficiency(orders);
        
        // Factor 2: Time window compatibility (0-1 score)
        double timeCompatibility = calculateTimeCompatibility(orders);
        
        // Factor 3: Restaurant preparation alignment (0-1 score)
        double preparationAlignment = calculatePreparationAlignment(orders);
        
        // Weighted average of all factors
        return (distanceEfficiency * 0.4) + (timeCompatibility * 0.3) + (preparationAlignment * 0.3);
    }
    
    private double calculateDistanceEfficiency(List<Order> orders) {
        if (orders.size() < 2) return 0.0;
        
        // Calculate total distance for individual deliveries
        double individualDistance = orders.size() * 5.0; // Assume 5km average per delivery
        
        // Calculate optimized route distance
        double optimizedDistance = calculateOptimizedRouteDistance(orders);
        
        // Efficiency = (saved distance / individual distance)
        return Math.max(0.0, (individualDistance - optimizedDistance) / individualDistance);
    }
    
    private double calculateTimeCompatibility(List<Order> orders) {
        // Check if all orders have compatible delivery time windows
        LocalDateTime earliestOrder = orders.stream()
            .map(Order::getOrderTime)
            .min(LocalDateTime::compareTo)
            .orElse(LocalDateTime.now());
        
        LocalDateTime latestOrder = orders.stream()
            .map(Order::getOrderTime)
            .max(LocalDateTime::compareTo)
            .orElse(LocalDateTime.now());
        
        // Time window in minutes
        long timeWindowMinutes = java.time.Duration.between(earliestOrder, latestOrder).toMinutes();
        
        // Perfect compatibility if within 15 minutes, decreasing score after that
        return Math.max(0.0, Math.min(1.0, (15.0 - timeWindowMinutes) / 15.0));
    }
    
    private double calculatePreparationAlignment(List<Order> orders) {
        // Simplified: assume all orders from same restaurant have good alignment
        // In reality, this would consider menu items, kitchen capacity, etc.
        Set<String> restaurants = orders.stream()
            .map(Order::getRestaurantId)
            .collect(Collectors.toSet());
        
        return restaurants.size() == 1 ? 1.0 : 0.5; // Same restaurant = perfect alignment
    }
    
    private double calculateOptimizedRouteDistance(List<Order> orders) {
        // Simplified TSP-like calculation
        // In production, this would use proper route optimization algorithms
        if (orders.size() < 2) return 0.0;
        
        double totalDistance = 0.0;
        for (int i = 0; i < orders.size() - 1; i++) {
            totalDistance += calculateDistance(
                orders.get(i).getDeliveryAddress(),
                orders.get(i + 1).getDeliveryAddress()
            );
        }
        
        return totalDistance + 2.0; // Add base distance from restaurant
    }
    
    private LocalDateTime calculateMergedDeliveryTime(List<Order> orders) {
        // Calculate estimated delivery time for merged orders
        LocalDateTime baseTime = LocalDateTime.now().plusMinutes(30); // Base preparation time
        
        // Add time based on number of stops
        int additionalMinutes = (orders.size() - 1) * 8; // 8 minutes per additional stop
        
        return baseTime.plusMinutes(additionalMinutes);
    }
    
    /**
     * Calculate distance between two addresses using Haversine formula
     */
    private double calculateDistance(Address addr1, Address addr2) {
        if (addr1.getLatitude() == null || addr1.getLongitude() == null ||
            addr2.getLatitude() == null || addr2.getLongitude() == null) {
            return Double.MAX_VALUE; // Cannot calculate distance
        }
        
        double lat1 = Math.toRadians(addr1.getLatitude());
        double lon1 = Math.toRadians(addr1.getLongitude());
        double lat2 = Math.toRadians(addr2.getLatitude());
        double lon2 = Math.toRadians(addr2.getLongitude());
        
        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;
        
        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dlon / 2) * Math.sin(dlon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return 6371 * c; // Earth's radius in kilometers
    }
    
    private void publishMergeEvent(String mergedOrderId, List<Order> mergedOrders) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "ORDERS_MERGED");
            event.put("mergedOrderId", mergedOrderId);
            event.put("orderIds", mergedOrders.stream().map(Order::getOrderId).collect(Collectors.toList()));
            event.put("orderCount", mergedOrders.size());
            event.put("restaurantId", mergedOrders.get(0).getRestaurantId());
            event.put("estimatedTimeSavings", calculateTimeSavings(mergedOrders));
            event.put("timestamp", LocalDateTime.now());
            
            kafkaTemplate.send("order-events", mergedOrderId, event);
        } catch (Exception e) {
            System.err.println("Failed to publish merge event: " + e.getMessage());
        }
    }
    
    private int calculateTimeSavings(List<Order> orders) {
        // Estimate time savings in minutes
        return (orders.size() - 1) * 12; // Assume 12 minutes saved per merged order
    }
}