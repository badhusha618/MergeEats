package com.mergeeats.restaurantservice.repository;

import com.mergeeats.common.models.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    
    List<Restaurant> findByOwnerId(String ownerId);
    
    List<Restaurant> findByIsActiveTrue();
    
    List<Restaurant> findByIsOpenTrue();
    
    List<Restaurant> findByAcceptsOnlineOrdersTrue();
    
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Restaurant> findByNameContainingIgnoreCase(String name);
    
    @Query("{'cuisineTypes': {$in: [?0]}}")
    List<Restaurant> findByCuisineType(String cuisineType);
    
    @Query("{'rating': {$gte: ?0}}")
    List<Restaurant> findByRatingGreaterThanEqual(Double minRating);
    
    @Query("{'address.city': ?0}")
    List<Restaurant> findByCity(String city);
    
    @Query("{'address.latitude': {$gte: ?0, $lte: ?1}, 'address.longitude': {$gte: ?2, $lte: ?3}}")
    List<Restaurant> findRestaurantsInArea(double minLat, double maxLat, double minLng, double maxLng);
    
    List<Restaurant> findByOwnerIdAndIsActiveTrue(String ownerId);
    
    @Query("{'isActive': true, 'isOpen': true, 'acceptsOnlineOrders': true}")
    List<Restaurant> findAvailableRestaurants();
}