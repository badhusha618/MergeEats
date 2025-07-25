package com.mergeeats.restaurantservice.service;

import com.mergeeats.common.models.Restaurant;
import com.mergeeats.restaurantservice.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestaurantService {
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setUpdatedAt(LocalDateTime.now());
        
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        
        // Publish restaurant created event
        publishRestaurantEvent("RESTAURANT_CREATED", savedRestaurant);
        
        return savedRestaurant;
    }
    
    public Restaurant getRestaurantById(String restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
    }
    
    public List<Restaurant> getRestaurantsByOwnerId(String ownerId) {
        return restaurantRepository.findByOwnerId(ownerId);
    }
    
    public List<Restaurant> getActiveRestaurants() {
        return restaurantRepository.findByIsActiveTrue();
    }
    
    public List<Restaurant> getAvailableRestaurants() {
        return restaurantRepository.findAvailableRestaurants();
    }
    
    public List<Restaurant> searchRestaurants(String name) {
        return restaurantRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<Restaurant> getRestaurantsByCuisine(String cuisineType) {
        return restaurantRepository.findByCuisineType(cuisineType);
    }
    
    public List<Restaurant> getRestaurantsByRating(Double minRating) {
        return restaurantRepository.findByRatingGreaterThanEqual(minRating);
    }
    
    public List<Restaurant> getRestaurantsByCity(String city) {
        return restaurantRepository.findByCity(city);
    }
    
    public List<Restaurant> getRestaurantsInArea(double lat, double lng, double radiusKm) {
        // Simple bounding box calculation (in production, use proper geospatial queries)
        double latDelta = radiusKm / 111.0; // Approximate km per degree latitude
        double lngDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(lat)));
        
        return restaurantRepository.findRestaurantsInArea(
            lat - latDelta, lat + latDelta,
            lng - lngDelta, lng + lngDelta
        );
    }
    
    public Restaurant updateRestaurant(String restaurantId, Restaurant updatedRestaurant) {
        Restaurant existingRestaurant = getRestaurantById(restaurantId);
        
        // Update fields
        if (updatedRestaurant.getName() != null) {
            existingRestaurant.setName(updatedRestaurant.getName());
        }
        if (updatedRestaurant.getDescription() != null) {
            existingRestaurant.setDescription(updatedRestaurant.getDescription());
        }
        if (updatedRestaurant.getAddress() != null) {
            existingRestaurant.setAddress(updatedRestaurant.getAddress());
        }
        if (updatedRestaurant.getPhoneNumber() != null) {
            existingRestaurant.setPhoneNumber(updatedRestaurant.getPhoneNumber());
        }
        if (updatedRestaurant.getEmail() != null) {
            existingRestaurant.setEmail(updatedRestaurant.getEmail());
        }
        if (updatedRestaurant.getCuisineTypes() != null) {
            existingRestaurant.setCuisineTypes(updatedRestaurant.getCuisineTypes());
        }
        if (updatedRestaurant.getOpeningTime() != null) {
            existingRestaurant.setOpeningTime(updatedRestaurant.getOpeningTime());
        }
        if (updatedRestaurant.getClosingTime() != null) {
            existingRestaurant.setClosingTime(updatedRestaurant.getClosingTime());
        }
        if (updatedRestaurant.getDeliveryRadius() != null) {
            existingRestaurant.setDeliveryRadius(updatedRestaurant.getDeliveryRadius());
        }
        if (updatedRestaurant.getMinimumOrderAmount() != null) {
            existingRestaurant.setMinimumOrderAmount(updatedRestaurant.getMinimumOrderAmount());
        }
        if (updatedRestaurant.getAveragePreparationTime() != null) {
            existingRestaurant.setAveragePreparationTime(updatedRestaurant.getAveragePreparationTime());
        }
        
        existingRestaurant.setUpdatedAt(LocalDateTime.now());
        
        Restaurant savedRestaurant = restaurantRepository.save(existingRestaurant);
        
        // Publish restaurant updated event
        publishRestaurantEvent("RESTAURANT_UPDATED", savedRestaurant);
        
        return savedRestaurant;
    }
    
    public Restaurant toggleRestaurantStatus(String restaurantId, boolean isOpen) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setOpen(isOpen);
        restaurant.setUpdatedAt(LocalDateTime.now());
        
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        
        // Publish status change event
        publishRestaurantEvent(isOpen ? "RESTAURANT_OPENED" : "RESTAURANT_CLOSED", savedRestaurant);
        
        return savedRestaurant;
    }
    
    public Restaurant activateRestaurant(String restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setActive(true);
        restaurant.setUpdatedAt(LocalDateTime.now());
        
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        
        // Publish activation event
        publishRestaurantEvent("RESTAURANT_ACTIVATED", savedRestaurant);
        
        return savedRestaurant;
    }
    
    public Restaurant deactivateRestaurant(String restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setActive(false);
        restaurant.setUpdatedAt(LocalDateTime.now());
        
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        
        // Publish deactivation event
        publishRestaurantEvent("RESTAURANT_DEACTIVATED", savedRestaurant);
        
        return savedRestaurant;
    }
    
    public void updateRestaurantRating(String restaurantId, double newRating, int totalReviews) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setRating(newRating);
        restaurant.setTotalReviews(totalReviews);
        restaurant.setUpdatedAt(LocalDateTime.now());
        
        restaurantRepository.save(restaurant);
        
        // Publish rating update event
        Map<String, Object> ratingEvent = new HashMap<>();
        ratingEvent.put("restaurantId", restaurantId);
        ratingEvent.put("newRating", newRating);
        ratingEvent.put("totalReviews", totalReviews);
        ratingEvent.put("timestamp", LocalDateTime.now());
        
        kafkaTemplate.send("restaurant-events", restaurantId, ratingEvent);
    }
    
    private void publishRestaurantEvent(String eventType, Restaurant restaurant) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventType);
            event.put("restaurantId", restaurant.getRestaurantId());
            event.put("ownerId", restaurant.getOwnerId());
            event.put("name", restaurant.getName());
            event.put("isActive", restaurant.isActive());
            event.put("isOpen", restaurant.isOpen());
            event.put("acceptsOnlineOrders", restaurant.isAcceptsOnlineOrders());
            event.put("timestamp", LocalDateTime.now());
            
            kafkaTemplate.send("restaurant-events", restaurant.getRestaurantId(), event);
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to publish restaurant event: " + e.getMessage());
        }
    }
}