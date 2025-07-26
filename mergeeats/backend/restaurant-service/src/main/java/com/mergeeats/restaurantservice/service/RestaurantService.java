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
        restaurant.setCuisine(request.getCuisine());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhone(request.getPhone());
        restaurant.setEmail(request.getEmail());
        restaurant.setOpeningHours(request.getOpeningHours());
        restaurant.setIsOpen(true);
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
            return restaurantRepository.findByCuisineAndAddressCityAndIsOpen(cuisine, location, isOpen, pageable);
        } else if (cuisine != null && location != null) {
            return restaurantRepository.findByCuisineAndAddressCity(cuisine, location, pageable);
        } else if (cuisine != null && isOpen != null) {
            return restaurantRepository.findByCuisineAndIsOpen(cuisine, isOpen, pageable);
        } else if (location != null && isOpen != null) {
            return restaurantRepository.findByAddressCityAndIsOpen(location, isOpen, pageable);
        } else if (cuisine != null) {
            return restaurantRepository.findByCuisine(cuisine, pageable);
        } else if (location != null) {
            return restaurantRepository.findByAddressCity(location, pageable);
        } else if (isOpen != null) {
            return restaurantRepository.findByIsOpen(isOpen, pageable);
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
            restaurant.setCuisine(request.getCuisine());
        }
        if (request.getAddress() != null) {
            restaurant.setAddress(request.getAddress());
        }
        if (request.getPhone() != null) {
            restaurant.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            restaurant.setEmail(request.getEmail());
        }
        if (request.getOpeningHours() != null) {
            restaurant.setOpeningHours(request.getOpeningHours());
        }
        
        restaurant.setUpdatedAt(LocalDateTime.now());
        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurant(String restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurantRepository.delete(restaurant);
    }

    public Restaurant addMenuItem(String restaurantId, MenuItemRequest request) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        
        var menuItem = new Restaurant.MenuItem();
        menuItem.setId(generateMenuItemId());
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setCategory(request.getCategory());
        menuItem.setIsVegetarian(request.getIsVegetarian());
        menuItem.setIsAvailable(true);
        menuItem.setImageUrl(request.getImageUrl());
        
        restaurant.getMenu().add(menuItem);
        restaurant.setUpdatedAt(LocalDateTime.now());
        
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateMenuItem(String restaurantId, String itemId, MenuItemRequest request) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        
        Optional<Restaurant.MenuItem> menuItemOpt = restaurant.getMenu().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
        
        if (menuItemOpt.isEmpty()) {
            throw new RuntimeException("Menu item not found");
        }
        
        Restaurant.MenuItem menuItem = menuItemOpt.get();
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setCategory(request.getCategory());
        menuItem.setIsVegetarian(request.getIsVegetarian());
        menuItem.setImageUrl(request.getImageUrl());
        
        restaurant.setUpdatedAt(LocalDateTime.now());
        return restaurantRepository.save(restaurant);
    }

    public Restaurant removeMenuItem(String restaurantId, String itemId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        
        restaurant.getMenu().removeIf(item -> item.getId().equals(itemId));
        restaurant.setUpdatedAt(LocalDateTime.now());
        
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurantStatus(String restaurantId, Boolean isOpen) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setIsOpen(isOpen);
        restaurant.setUpdatedAt(LocalDateTime.now());
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> searchRestaurants(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restaurantRepository.findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(query, query, pageable);
    }

    public List<Restaurant> getNearbyRestaurants(Double latitude, Double longitude, Double radiusKm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restaurantRepository.findNearbyRestaurants(latitude, longitude, radiusKm, pageable);
    }

    private String generateMenuItemId() {
        return "menu_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
} 