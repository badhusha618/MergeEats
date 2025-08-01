package com.mergeeats.restaurantservice.service;

import com.mergeeats.common.models.Restaurant;
import com.mergeeats.restaurantservice.dto.CreateRestaurantRequest;
import com.mergeeats.restaurantservice.dto.UpdateRestaurantRequest;
import com.mergeeats.restaurantservice.dto.MenuItemRequest;
import com.mergeeats.restaurantservice.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public Restaurant createRestaurant(CreateRestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        // Set cuisine types as a list
        if (request.getCuisine() != null) {
            restaurant.setCuisineTypes(List.of(request.getCuisine()));
        }
        restaurant.setAddress(request.getAddress());
        restaurant.setPhoneNumber(request.getPhone());
        restaurant.setEmail(request.getEmail());
        // Note: Restaurant model doesn't have openingHours field, using openingTime/closingTime instead
        restaurant.setOpen(true);
        restaurant.setRating(0.0);
        restaurant.setTotalReviews(0);
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setUpdatedAt(LocalDateTime.now());
        
        return restaurantRepository.save(restaurant);
    }

    public Restaurant getRestaurantById(String restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public List<Restaurant> getAllRestaurants(String cuisine, String location, Boolean isOpen, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        if (cuisine != null && location != null && isOpen != null) {
            return restaurantRepository.findByCuisineAndAddressCityAndIsOpen(cuisine, location, isOpen, pageable).getContent();
        } else if (cuisine != null && location != null) {
            return restaurantRepository.findByCuisineAndAddressCity(cuisine, location, pageable).getContent();
        } else if (cuisine != null && isOpen != null) {
            return restaurantRepository.findByCuisineAndIsOpen(cuisine, isOpen, pageable).getContent();
        } else if (location != null && isOpen != null) {
            return restaurantRepository.findByAddressCityAndIsOpen(location, isOpen, pageable).getContent();
        } else if (cuisine != null) {
            return restaurantRepository.findByCuisine(cuisine, pageable).getContent();
        } else if (location != null) {
            return restaurantRepository.findByAddressCity(location, pageable).getContent();
        } else if (isOpen != null) {
            return restaurantRepository.findByIsOpen(isOpen, pageable).getContent();
        } else {
            return restaurantRepository.findAll(pageable).getContent();
        }
    }

    public Restaurant updateRestaurant(String restaurantId, UpdateRestaurantRequest request) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        
        if (request.getName() != null) {
            restaurant.setName(request.getName());
        }
        if (request.getDescription() != null) {
            restaurant.setDescription(request.getDescription());
        }
        if (request.getCuisine() != null) {
            restaurant.setCuisineTypes(List.of(request.getCuisine()));
        }
        if (request.getAddress() != null) {
            restaurant.setAddress(request.getAddress());
        }
        if (request.getPhone() != null) {
            restaurant.setPhoneNumber(request.getPhone());
        }
        if (request.getEmail() != null) {
            restaurant.setEmail(request.getEmail());
        }
        // Note: Restaurant model doesn't have openingHours field
        
        restaurant.setUpdatedAt(LocalDateTime.now());
        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurant(String restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurantRepository.delete(restaurant);
    }

    // Note: Menu item operations are not supported in the current Restaurant model
    // These methods would need to be implemented when menu items are added to the Restaurant model

    public Restaurant updateRestaurantStatus(String restaurantId, Boolean isOpen) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setOpen(isOpen);
        restaurant.setUpdatedAt(LocalDateTime.now());
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> searchRestaurants(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restaurantRepository.findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(query, query, pageable).getContent();
    }

    public List<Restaurant> getNearbyRestaurants(Double latitude, Double longitude, Double radiusKm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restaurantRepository.findNearbyRestaurants(latitude, longitude, radiusKm, pageable).getContent();
    }
} 