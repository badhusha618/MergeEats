package com.mergeeats.restaurantservice.repository;

import com.mergeeats.common.models.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, String> {

    Page<Restaurant> findByCuisine(String cuisine, Pageable pageable);
    
    Page<Restaurant> findByAddressCity(String city, Pageable pageable);
    
    Page<Restaurant> findByIsOpen(Boolean isOpen, Pageable pageable);
    
    Page<Restaurant> findByCuisineAndAddressCity(String cuisine, String city, Pageable pageable);
    
    Page<Restaurant> findByCuisineAndIsOpen(String cuisine, Boolean isOpen, Pageable pageable);
    
    Page<Restaurant> findByAddressCityAndIsOpen(String city, Boolean isOpen, Pageable pageable);
    
    Page<Restaurant> findByCuisineAndAddressCityAndIsOpen(String cuisine, String city, Boolean isOpen, Pageable pageable);
    
    Page<Restaurant> findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(String name, String cuisine, Pageable pageable);
    
    @Query("{'address.latitude': {$gte: ?0 - ?2, $lte: ?0 + ?2}, 'address.longitude': {$gte: ?1 - ?2, $lte: ?1 + ?2}}")
    Page<Restaurant> findNearbyRestaurants(Double latitude, Double longitude, Double radiusKm, Pageable pageable);
    
    List<Restaurant> findByRatingGreaterThanEqual(Double minRating);
    
    List<Restaurant> findByTotalReviewsGreaterThanEqual(Integer minReviews);
} 