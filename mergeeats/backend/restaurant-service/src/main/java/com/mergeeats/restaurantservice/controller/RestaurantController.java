package com.mergeeats.restaurantservice.controller;

import com.mergeeats.common.models.Restaurant;
import com.mergeeats.restaurantservice.dto.CreateRestaurantRequest;
import com.mergeeats.restaurantservice.dto.UpdateRestaurantRequest;
import com.mergeeats.restaurantservice.dto.MenuItemRequest;
import com.mergeeats.restaurantservice.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurant Management", 
     description = "**Comprehensive Restaurant Management APIs**\n\n" +
                  "This controller provides all restaurant-related operations including:\n" +
                  "- Restaurant CRUD operations\n" +
                  "- Menu management\n" +
                  "- Restaurant search and filtering\n" +
                  "- Location-based restaurant discovery\n" +
                  "- Restaurant status management")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PostMapping
    @Operation(
        summary = "Create a new restaurant",
        description = "**Creates a new restaurant with comprehensive details**\n\n" +
                     "This endpoint allows restaurant owners to register their establishment with:\n" +
                     "- Basic restaurant information\n" +
                     "- Location and contact details\n" +
                     "- Operating hours\n" +
                     "- Cuisine type and specialties",
        tags = {"Restaurant Registration"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restaurant created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Restaurant.class),
                examples = @ExampleObject(
                    name = "Successful Creation",
                    value = """
                        {
                          "id": "restaurant_123456789",
                          "name": "Pizza Palace",
                          "description": "Authentic Italian pizza and pasta",
                          "cuisineType": "Italian",
                          "address": "123 Main St, City, State 12345",
                          "latitude": 40.7128,
                          "longitude": -74.0060,
                          "phoneNumber": "+1-555-123-4567",
                          "email": "info@pizzapalace.com",
                          "website": "https://www.pizzapalace.com",
                          "isOpen": true,
                          "rating": 4.5,
                          "deliveryFee": 2.99,
                          "minimumOrder": 10.00
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid restaurant data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                        {
                          "message": "Validation failed",
                          "errors": [
                            "Restaurant name is required",
                            "Cuisine type is required",
                            "Address is required"
                          ]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Restaurant> createRestaurant(
        @Parameter(
            description = "Restaurant creation data",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Restaurant Creation",
                    value = """
                        {
                          "name": "Pizza Palace",
                          "description": "Authentic Italian pizza and pasta",
                          "cuisineType": "Italian",
                          "address": "123 Main St, City, State 12345",
                          "latitude": 40.7128,
                          "longitude": -74.0060,
                          "phoneNumber": "+1-555-123-4567",
                          "email": "info@pizzapalace.com",
                          "website": "https://www.pizzapalace.com"
                        }
                        """
                )
            )
        )
        @Valid @RequestBody CreateRestaurantRequest request) {
        Restaurant restaurant = restaurantService.createRestaurant(request);
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping("/{restaurantId}")
    @Operation(
        summary = "Get restaurant by ID",
        description = "**Retrieves detailed restaurant information by ID**\n\n" +
                     "Returns comprehensive restaurant data including:\n" +
                     "- Basic restaurant information\n" +
                     "- Menu items and categories\n" +
                     "- Operating hours and status\n" +
                     "- Ratings and reviews summary\n" +
                     "- Delivery information",
        tags = {"Restaurant Information"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restaurant found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Restaurant.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Restaurant not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "Restaurant not found",
                          "errors": ["Restaurant with ID restaurant_123456789 does not exist"]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Restaurant> getRestaurant(
        @Parameter(example = "restaurant_123456789", description = "Unique restaurant identifier")
        @PathVariable String restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping
    @Operation(summary = "Get all restaurants with optional filters")
    public ResponseEntity<List<Restaurant>> getAllRestaurants(
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean isOpen,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants(cuisine, location, isOpen, page, size);
        return ResponseEntity.ok(restaurants);
    }

    @PutMapping("/{restaurantId}")
    @Operation(summary = "Update restaurant details")
    public ResponseEntity<Restaurant> updateRestaurant(
            @PathVariable String restaurantId,
            @Valid @RequestBody UpdateRestaurantRequest request) {
        Restaurant restaurant = restaurantService.updateRestaurant(restaurantId, request);
        return ResponseEntity.ok(restaurant);
    }

    @DeleteMapping("/{restaurantId}")
    @Operation(summary = "Delete restaurant")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable String restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{restaurantId}/menu")
    @Operation(summary = "Add menu item to restaurant")
    public ResponseEntity<Restaurant> addMenuItem(
            @PathVariable String restaurantId,
            @Valid @RequestBody MenuItemRequest request) {
        Restaurant restaurant = restaurantService.addMenuItem(restaurantId, request);
        return ResponseEntity.ok(restaurant);
    }

    @PutMapping("/{restaurantId}/menu/{itemId}")
    @Operation(summary = "Update menu item")
    public ResponseEntity<Restaurant> updateMenuItem(
            @PathVariable String restaurantId,
            @PathVariable String itemId,
            @Valid @RequestBody MenuItemRequest request) {
        Restaurant restaurant = restaurantService.updateMenuItem(restaurantId, itemId, request);
        return ResponseEntity.ok(restaurant);
    }

    @DeleteMapping("/{restaurantId}/menu/{itemId}")
    @Operation(summary = "Remove menu item")
    public ResponseEntity<Restaurant> removeMenuItem(
            @PathVariable String restaurantId,
            @PathVariable String itemId) {
        Restaurant restaurant = restaurantService.removeMenuItem(restaurantId, itemId);
        return ResponseEntity.ok(restaurant);
    }

    @PutMapping("/{restaurantId}/status")
    @Operation(summary = "Update restaurant status (open/closed)")
    public ResponseEntity<Restaurant> updateRestaurantStatus(
            @PathVariable String restaurantId,
            @RequestParam Boolean isOpen) {
        Restaurant restaurant = restaurantService.updateRestaurantStatus(restaurantId, isOpen);
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping("/search")
    @Operation(summary = "Search restaurants by name or cuisine")
    public ResponseEntity<List<Restaurant>> searchRestaurants(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Restaurant> restaurants = restaurantService.searchRestaurants(query, page, size);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find restaurants near a location")
    public ResponseEntity<List<Restaurant>> getNearbyRestaurants(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Restaurant> restaurants = restaurantService.getNearbyRestaurants(latitude, longitude, radiusKm, page, size);
        return ResponseEntity.ok(restaurants);
    }
} 