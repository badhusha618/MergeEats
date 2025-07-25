package com.mergeeats.userservice.repository;

import com.mergeeats.common.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    List<User> findByIsActiveTrue();
    
    @Query("{'roles': ?0}")
    List<User> findByRole(String role);
    
    @Query("{'fullName': {$regex: ?0, $options: 'i'}}")
    List<User> findByFullNameContainingIgnoreCase(String name);
    
    @Query("{'isEmailVerified': ?0}")
    List<User> findByEmailVerificationStatus(boolean isVerified);
    
    @Query("{'createdAt': {$gte: ?0, $lte: ?1}}")
    List<User> findByCreatedAtBetween(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}