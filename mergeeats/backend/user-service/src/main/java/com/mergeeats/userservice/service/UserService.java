package com.mergeeats.userservice.service;

import com.mergeeats.common.models.User;
import com.mergeeats.userservice.dto.AuthResponse;
import com.mergeeats.userservice.dto.LoginRequest;
import com.mergeeats.userservice.dto.UserRegistrationRequest;
import com.mergeeats.userservice.repository.UserRepository;
import com.mergeeats.userservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public AuthResponse registerUser(UserRegistrationRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        
        // Set default role if not provided
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            user.setRoles(Set.of("CUSTOMER"));
        } else {
            user.setRoles(request.getRoles());
        }
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Generate JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", savedUser.getUserId());
        claims.put("roles", savedUser.getRoles());
        String token = jwtUtil.generateToken(savedUser.getEmail(), claims);
        
        // Publish user registration event
        publishUserEvent("USER_REGISTERED", savedUser);
        
        return new AuthResponse(token, savedUser, "User registered successfully");
    }
    
    public AuthResponse loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        if (!user.isActive()) {
            throw new RuntimeException("User account is deactivated");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        // Generate JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("roles", user.getRoles());
        String token = jwtUtil.generateToken(user.getEmail(), claims);
        
        // Update last login time (if you want to track this)
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Publish user login event
        publishUserEvent("USER_LOGGED_IN", user);
        
        return new AuthResponse(token, user, "Login successful");
    }
    
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
    
    public User updateUser(String userId, User updatedUser) {
        User existingUser = getUserById(userId);
        
        // Update fields
        if (updatedUser.getFullName() != null) {
            existingUser.setFullName(updatedUser.getFullName());
        }
        if (updatedUser.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        }
        if (updatedUser.getAddress() != null) {
            existingUser.setAddress(updatedUser.getAddress());
        }
        
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(existingUser);
        
        // Publish user update event
        publishUserEvent("USER_UPDATED", savedUser);
        
        return savedUser;
    }
    
    public void deactivateUser(String userId) {
        User user = getUserById(userId);
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        // Publish user deactivation event
        publishUserEvent("USER_DEACTIVATED", user);
    }
    
    public void activateUser(String userId) {
        User user = getUserById(userId);
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        // Publish user activation event
        publishUserEvent("USER_ACTIVATED", user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }
    
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    public boolean verifyEmail(String userId) {
        User user = getUserById(userId);
        user.setEmailVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        // Publish email verification event
        publishUserEvent("EMAIL_VERIFIED", user);
        
        return true;
    }
    
    public boolean verifyPhone(String userId) {
        User user = getUserById(userId);
        user.setPhoneVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        // Publish phone verification event
        publishUserEvent("PHONE_VERIFIED", user);
        
        return true;
    }
    
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        // Publish password change event
        publishUserEvent("PASSWORD_CHANGED", user);
        
        return true;
    }
    
    private void publishUserEvent(String eventType, User user) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventType);
            event.put("userId", user.getUserId());
            event.put("email", user.getEmail());
            event.put("fullName", user.getFullName());
            event.put("roles", user.getRoles());
            event.put("timestamp", LocalDateTime.now());
            
            kafkaTemplate.send("user-events", user.getUserId(), event);
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to publish user event: " + e.getMessage());
        }
    }
}