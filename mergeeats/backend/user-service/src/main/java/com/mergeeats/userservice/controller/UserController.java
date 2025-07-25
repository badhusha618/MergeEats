package com.mergeeats.userservice.controller;

import com.mergeeats.common.models.User;
import com.mergeeats.userservice.dto.AuthResponse;
import com.mergeeats.userservice.dto.LoginRequest;
import com.mergeeats.userservice.dto.UserRegistrationRequest;
import com.mergeeats.userservice.service.UserService;
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
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for user registration, authentication, and profile management")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns JWT token")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            AuthResponse response = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.loginUser(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieves user information by user ID")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieves user information by email")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        try {
            User user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{userId}")
    @Operation(summary = "Update user profile", description = "Updates user profile information")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody User updatedUser) {
        try {
            User user = userService.updateUser(userId, updatedUser);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{userId}/deactivate")
    @Operation(summary = "Deactivate user", description = "Deactivates a user account")
    public ResponseEntity<String> deactivateUser(@PathVariable String userId) {
        try {
            userService.deactivateUser(userId);
            return ResponseEntity.ok("User deactivated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{userId}/activate")
    @Operation(summary = "Activate user", description = "Activates a user account")
    public ResponseEntity<String> activateUser(@PathVariable String userId) {
        try {
            userService.activateUser(userId);
            return ResponseEntity.ok("User activated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves all users (admin only)")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active users", description = "Retrieves all active users")
    public ResponseEntity<List<User>> getActiveUsers() {
        List<User> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Retrieves users by their role")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
    
    @PostMapping("/{userId}/verify-email")
    @Operation(summary = "Verify email", description = "Marks user email as verified")
    public ResponseEntity<String> verifyEmail(@PathVariable String userId) {
        try {
            userService.verifyEmail(userId);
            return ResponseEntity.ok("Email verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{userId}/verify-phone")
    @Operation(summary = "Verify phone", description = "Marks user phone as verified")
    public ResponseEntity<String> verifyPhone(@PathVariable String userId) {
        try {
            userService.verifyPhone(userId);
            return ResponseEntity.ok("Phone verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{userId}/change-password")
    @Operation(summary = "Change password", description = "Changes user password")
    public ResponseEntity<String> changePassword(
            @PathVariable String userId,
            @RequestBody Map<String, String> passwordData) {
        try {
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");
            
            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Both oldPassword and newPassword are required");
            }
            
            userService.changePassword(userId, oldPassword, newPassword);
            return ResponseEntity.ok("Password changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Service health check endpoint")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("User Service is running");
    }
}