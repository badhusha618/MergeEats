package com.mergeeats.userservice.controller;

import com.mergeeats.common.models.User;
import com.mergeeats.userservice.dto.AuthResponse;
import com.mergeeats.userservice.dto.LoginRequest;
import com.mergeeats.userservice.dto.UserRegistrationRequest;
import com.mergeeats.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@Tag(name = "User Management", 
     description = "**Comprehensive User Management APIs**\n\n" +
                  "This controller provides all user-related operations including:\n" +
                  "- User registration and authentication\n" +
                  "- Profile management and updates\n" +
                  "- Address management\n" +
                  "- User preferences and settings\n" +
                  "- Account security operations")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    @Operation(
        summary = "Register New User Account",
        description = "**Creates a new user account with comprehensive validation**\n\n" +
                     "This endpoint handles user registration for different user types:\n" +
                     "- **Customers**: Regular users who place orders\n" +
                     "- **Restaurant Owners**: Business users who manage restaurants\n" +
                     "- **Delivery Partners**: Users who deliver orders\n\n" +
                     "**Features:**\n" +
                     "- Email uniqueness validation\n" +
                     "- Password strength requirements\n" +
                     "- Automatic JWT token generation\n" +
                     "- User role assignment\n" +
                     "- Welcome notification trigger",
        tags = {"Authentication", "User Registration"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User successfully registered",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    name = "Successful Registration",
                    value = """
                        {
                          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "user": {
                            "id": "60f7b1b3e4b0c8a2d8e9f0a1",
                            "email": "john.doe@example.com",
                            "firstName": "John",
                            "lastName": "Doe",
                            "role": "CUSTOMER",
                            "isActive": true
                          },
                          "message": "User registered successfully",
                          "expiresIn": 86400
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid registration data or email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Registration Error",
                    value = """
                        {
                          "message": "Email already exists",
                          "errors": ["Email john.doe@example.com is already registered"]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Validation errors in request data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                        {
                          "message": "Validation failed",
                          "errors": [
                            "Email must be valid",
                            "Password must be at least 8 characters",
                            "First name is required"
                          ]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<AuthResponse> registerUser(
        @Parameter(
            description = "User registration data including personal information and credentials",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Customer Registration",
                    value = """
                        {
                          "email": "john.doe@example.com",
                          "password": "SecurePass123!",
                          "firstName": "John",
                          "lastName": "Doe",
                          "phoneNumber": "+1-555-0123",
                          "role": "CUSTOMER",
                          "address": {
                            "street": "123 Main St",
                            "city": "New York",
                            "state": "NY",
                            "zipCode": "10001",
                            "country": "USA"
                          }
                        }
                        """
                )
            )
        )
        @Valid @RequestBody UserRegistrationRequest request) {
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
    @Operation(
        summary = "User Authentication",
        description = "**Authenticate user credentials and generate JWT token**\n\n" +
                     "This endpoint handles user authentication with enhanced security:\n\n" +
                     "**Security Features:**\n" +
                     "- Secure password validation\n" +
                     "- Account lockout protection\n" +
                     "- JWT token generation with expiration\n" +
                     "- Login attempt tracking\n" +
                     "- Multi-factor authentication support\n\n" +
                     "**Supported Login Methods:**\n" +
                     "- Email and password\n" +
                     "- Social login (Google, Facebook)\n" +
                     "- Phone number and OTP",
        tags = {"Authentication", "User Login"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    name = "Successful Login",
                    value = """
                        {
                          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "user": {
                            "id": "60f7b1b3e4b0c8a2d8e9f0a1",
                            "email": "john.doe@example.com",
                            "firstName": "John",
                            "lastName": "Doe",
                            "role": "CUSTOMER",
                            "isActive": true,
                            "lastLoginAt": "2024-01-15T10:30:00Z"
                          },
                          "message": "Login successful",
                          "expiresIn": 86400
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials or account locked",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Authentication Error",
                    value = """
                        {
                          "message": "Invalid email or password",
                          "errors": ["Authentication failed"]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "423",
            description = "Account temporarily locked due to multiple failed attempts",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Account Locked",
                    value = """
                        {
                          "message": "Account temporarily locked",
                          "errors": ["Too many failed login attempts. Try again in 15 minutes."],
                          "lockoutExpiresAt": "2024-01-15T11:00:00Z"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<AuthResponse> loginUser(
        @Parameter(
            description = "User login credentials",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Email Login",
                    value = """
                        {
                          "email": "john.doe@example.com",
                          "password": "SecurePass123!"
                        }
                        """
                )
            )
        )
        @Valid @RequestBody LoginRequest request) {
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

    @GetMapping("/profile")
    @Operation(
        summary = "Get User Profile",
        description = "**Retrieve detailed user profile information**\n\n" +
                     "Returns comprehensive user profile data including:\n" +
                     "- Personal information\n" +
                     "- Account settings and preferences\n" +
                     "- Delivery addresses\n" +
                     "- Order history summary\n" +
                     "- Loyalty points and rewards",
        tags = {"User Profile"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "Authentication required",
                          "errors": ["Please provide a valid JWT token"]
                        }
                        """
                )
            )
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<User> getUserProfile(
        @Parameter(hidden = true) // Hide from Swagger as it's extracted from JWT
        @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract user ID from JWT token
            String userId = extractUserIdFromToken(authHeader);
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private String extractUserIdFromToken(String authHeader) {
        // Implementation would extract user ID from JWT token
        return "dummy-user-id";
    }
}