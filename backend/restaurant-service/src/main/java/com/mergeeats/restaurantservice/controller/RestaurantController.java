package com.mergeeats.restaurantservice.controller;

import com.mergeeats.common.models.Restaurant;
import com.mergeeats.restaurantservice.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurant Management", description = "APIs for restaurant management and discovery")
public class RestaurantController {
    
    @Autowired
    private RestaurantService restaurantService;
    
    @PostMapping
    @Operation(summary = "Create restaurant", description = "Creates a new restaurant")
    public ResponseEntity<Restaurant> createRestaurant(@Valid @RequestBody Restaurant restaurant) {
        try {
            Restaurant createdRestaurant = restaurantService.createRestaurant(restaurant);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRestaurant);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{restaurantId}")
    @Operation(summary = "Get restaurant by ID", description = "Retrieves restaurant details by ID")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable String restaurantId) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            return ResponseEntity.ok(restaurant);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get restaurants by owner", description = "Retrieves all restaurants owned by a specific user")
    public ResponseEntity<List<Restaurant>> getRestaurantsByOwnerId(@PathVariable String ownerId) {
        List<Restaurant> restaurants = restaurantService.getRestaurantsByOwnerId(ownerId);
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping
    @Operation(summary = "Get all active restaurants", description = "Retrieves all active restaurants")
    public ResponseEntity<List<Restaurant>> getActiveRestaurants() {
        List<Restaurant> restaurants = restaurantService.getActiveRestaurants();
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/available")
    @Operation(summary = "Get available restaurants", description = "Retrieves restaurants that are active, open, and accepting orders")
    public ResponseEntity<List<Restaurant>> getAvailableRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAvailableRestaurants();
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search restaurants", description = "Search restaurants by name")
    public ResponseEntity<List<Restaurant>> searchRestaurants(@RequestParam String name) {
        List<Restaurant> restaurants = restaurantService.searchRestaurants(name);
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/cuisine/{cuisineType}")
    @Operation(summary = "Get restaurants by cuisine", description = "Retrieves restaurants by cuisine type")
    public ResponseEntity<List<Restaurant>> getRestaurantsByCuisine(@PathVariable String cuisineType) {
        List<Restaurant> restaurants = restaurantService.getRestaurantsByCuisine(cuisineType);
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/rating/{minRating}")
    @Operation(summary = "Get restaurants by rating", description = "Retrieves restaurants with minimum rating")
    public ResponseEntity<List<Restaurant>> getRestaurantsByRating(@PathVariable Double minRating) {
        List<Restaurant> restaurants = restaurantService.getRestaurantsByRating(minRating);
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/city/{city}")
    @Operation(summary = "Get restaurants by city", description = "Retrieves restaurants in a specific city")
    public ResponseEntity<List<Restaurant>> getRestaurantsByCity(@PathVariable String city) {
        List<Restaurant> restaurants = restaurantService.getRestaurantsByCity(city);
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/nearby")
    @Operation(summary = "Get nearby restaurants", description = "Retrieves restaurants within a specified radius")
    public ResponseEntity<List<Restaurant>> getNearbyRestaurants(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10.0") double radiusKm) {
        List<Restaurant> restaurants = restaurantService.getRestaurantsInArea(lat, lng, radiusKm);
        return ResponseEntity.ok(restaurants);
    }
    
    @PutMapping("/{restaurantId}")
    @Operation(summary = "Update restaurant", description = "Updates restaurant information")
    public ResponseEntity<Restaurant> updateRestaurant(
            @PathVariable String restaurantId,
            @RequestBody Restaurant updatedRestaurant) {
        try {
            Restaurant restaurant = restaurantService.updateRestaurant(restaurantId, updatedRestaurant);
            return ResponseEntity.ok(restaurant);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{restaurantId}/toggle-status")
    @Operation(summary = "Toggle restaurant status", description = "Opens or closes a restaurant")
    public ResponseEntity<Restaurant> toggleRestaurantStatus(
            @PathVariable String restaurantId,
            @RequestBody Map<String, Boolean> statusData) {
        try {
            boolean isOpen = statusData.getOrDefault("isOpen", false);
            Restaurant restaurant = restaurantService.toggleRestaurantStatus(restaurantId, isOpen);
            return ResponseEntity.ok(restaurant);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{restaurantId}/activate")
    @Operation(summary = "Activate restaurant", description = "Activates a restaurant")
    public ResponseEntity<Restaurant> activateRestaurant(@PathVariable String restaurantId) {
        try {
            Restaurant restaurant = restaurantService.activateRestaurant(restaurantId);
            return ResponseEntity.ok(restaurant);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{restaurantId}/deactivate")
    @Operation(summary = "Deactivate restaurant", description = "Deactivates a restaurant")
    public ResponseEntity<Restaurant> deactivateRestaurant(@PathVariable String restaurantId) {
        try {
            Restaurant restaurant = restaurantService.deactivateRestaurant(restaurantId);
            return ResponseEntity.ok(restaurant);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{restaurantId}/rating")
    @Operation(summary = "Update restaurant rating", description = "Updates restaurant rating and review count")
    public ResponseEntity<String> updateRestaurantRating(
            @PathVariable String restaurantId,
            @RequestBody Map<String, Object> ratingData) {
        try {
            double newRating = ((Number) ratingData.get("rating")).doubleValue();
            int totalReviews = ((Number) ratingData.get("totalReviews")).intValue();
            
            restaurantService.updateRestaurantRating(restaurantId, newRating, totalReviews);
            return ResponseEntity.ok("Restaurant rating updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid rating data");
        }
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Service health check endpoint")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Restaurant Service is running");
    }
}