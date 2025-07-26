package com.mergeeats.common.swagger;

/**
 * Centralized Swagger Examples and Documentation Constants
 * Provides standardized examples and documentation strings for consistent API documentation
 */
public class SwaggerExamples {

    // User Service Examples
    public static class User {
        public static final String REGISTRATION_REQUEST = """
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
              },
              "preferences": {
                "dietaryRestrictions": ["VEGETARIAN"],
                "cuisinePreferences": ["ITALIAN", "CHINESE"],
                "notificationSettings": {
                  "email": true,
                  "sms": true,
                  "push": true
                }
              }
            }
            """;

        public static final String LOGIN_REQUEST = """
            {
              "email": "john.doe@example.com",
              "password": "SecurePass123!"
            }
            """;

        public static final String AUTH_RESPONSE = """
            {
              "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2MGY3YjFiM2U0YjBjOGEyZDhlOWYwYTEiLCJlbWFpbCI6ImpvaG4uZG9lQGV4YW1wbGUuY29tIiwicm9sZSI6IkNVU1RPTUVSIiwiaWF0IjoxNjQwOTk1MjAwLCJleHAiOjE2NDEwODE2MDB9.example",
              "user": {
                "id": "60f7b1b3e4b0c8a2d8e9f0a1",
                "email": "john.doe@example.com",
                "firstName": "John",
                "lastName": "Doe",
                "role": "CUSTOMER",
                "isActive": true,
                "createdAt": "2024-01-15T10:00:00Z",
                "lastLoginAt": "2024-01-15T10:30:00Z"
              },
              "message": "Authentication successful",
              "expiresIn": 86400
            }
            """;
    }

    // Order Service Examples
    public static class Order {
        public static final String CREATE_ORDER_REQUEST = """
            {
              "restaurantId": "60f7b1b3e4b0c8a2d8e9f0a2",
              "customerId": "60f7b1b3e4b0c8a2d8e9f0a1",
              "items": [
                {
                  "menuItemId": "60f7b1b3e4b0c8a2d8e9f0a3",
                  "quantity": 2,
                  "price": 12.99,
                  "customizations": [
                    {
                      "name": "Spice Level",
                      "value": "Medium",
                      "additionalCost": 0.0
                    }
                  ],
                  "specialInstructions": "Extra sauce on the side"
                }
              ],
              "deliveryAddress": {
                "street": "123 Main St",
                "city": "New York",
                "state": "NY",
                "zipCode": "10001",
                "country": "USA",
                "instructions": "Ring doorbell twice"
              },
              "paymentMethod": "CREDIT_CARD",
              "specialInstructions": "Please call when arriving",
              "scheduledDeliveryTime": "2024-01-15T19:30:00Z"
            }
            """;

        public static final String ORDER_RESPONSE = """
            {
              "id": "60f7b1b3e4b0c8a2d8e9f0a4",
              "orderNumber": "ME-2024-001234",
              "status": "CONFIRMED",
              "restaurantId": "60f7b1b3e4b0c8a2d8e9f0a2",
              "customerId": "60f7b1b3e4b0c8a2d8e9f0a1",
              "items": [
                {
                  "menuItemId": "60f7b1b3e4b0c8a2d8e9f0a3",
                  "name": "Chicken Tikka Masala",
                  "quantity": 2,
                  "unitPrice": 12.99,
                  "totalPrice": 25.98
                }
              ],
              "subtotal": 25.98,
              "taxAmount": 2.34,
              "deliveryFee": 3.99,
              "totalAmount": 32.31,
              "estimatedDeliveryTime": "2024-01-15T19:45:00Z",
              "createdAt": "2024-01-15T18:30:00Z",
              "updatedAt": "2024-01-15T18:32:00Z"
            }
            """;
    }

    // Restaurant Service Examples
    public static class Restaurant {
        public static final String RESTAURANT_REQUEST = """
            {
              "name": "Spice Garden Indian Cuisine",
              "description": "Authentic Indian flavors with modern presentation",
              "cuisineType": "INDIAN",
              "address": {
                "street": "456 Food Court Ave",
                "city": "New York",
                "state": "NY",
                "zipCode": "10002",
                "country": "USA"
              },
              "contactInfo": {
                "phone": "+1-555-0199",
                "email": "info@spicegarden.com",
                "website": "https://spicegarden.com"
              },
              "operatingHours": {
                "monday": {"open": "11:00", "close": "22:00"},
                "tuesday": {"open": "11:00", "close": "22:00"},
                "wednesday": {"open": "11:00", "close": "22:00"},
                "thursday": {"open": "11:00", "close": "22:00"},
                "friday": {"open": "11:00", "close": "23:00"},
                "saturday": {"open": "11:00", "close": "23:00"},
                "sunday": {"open": "12:00", "close": "21:00"}
              },
              "deliveryRadius": 5.0,
              "minimumOrderAmount": 15.00,
              "averageDeliveryTime": 35,
              "isActive": true
            }
            """;

        public static final String MENU_ITEM_REQUEST = """
            {
              "name": "Chicken Tikka Masala",
              "description": "Tender chicken pieces in creamy tomato-based curry sauce",
              "category": "MAIN_COURSE",
              "price": 12.99,
              "ingredients": ["Chicken", "Tomatoes", "Cream", "Onions", "Garlic", "Ginger", "Spices"],
              "allergens": ["DAIRY"],
              "dietaryInfo": ["GLUTEN_FREE"],
              "spiceLevel": "MEDIUM",
              "preparationTime": 15,
              "calories": 420,
              "isVegetarian": false,
              "isVegan": false,
              "isAvailable": true,
              "customizations": [
                {
                  "name": "Spice Level",
                  "options": ["MILD", "MEDIUM", "HOT", "EXTRA_HOT"],
                  "required": false,
                  "additionalCost": 0.0
                }
              ]
            }
            """;
    }

    // Payment Service Examples
    public static class Payment {
        public static final String PAYMENT_REQUEST = """
            {
              "orderId": "60f7b1b3e4b0c8a2d8e9f0a4",
              "amount": 32.31,
              "currency": "USD",
              "paymentMethod": "CREDIT_CARD",
              "paymentType": "ORDER_PAYMENT",
              "cardDetails": {
                "cardNumber": "4111111111111111",
                "expiryMonth": "12",
                "expiryYear": "2025",
                "cvv": "123",
                "cardholderName": "John Doe"
              },
              "billingAddress": {
                "street": "123 Main St",
                "city": "New York",
                "state": "NY",
                "zipCode": "10001",
                "country": "USA"
              },
              "description": "Payment for Order #ME-2024-001234"
            }
            """;

        public static final String PAYMENT_RESPONSE = """
            {
              "id": "60f7b1b3e4b0c8a2d8e9f0a5",
              "orderId": "60f7b1b3e4b0c8a2d8e9f0a4",
              "transactionId": "TXN-2024-001234",
              "status": "COMPLETED",
              "amount": 32.31,
              "currency": "USD",
              "paymentMethod": "CREDIT_CARD",
              "gatewayResponse": {
                "gatewayTransactionId": "pi_1234567890",
                "authorizationCode": "AUTH123",
                "responseCode": "00",
                "responseMessage": "Approved"
              },
              "processedAt": "2024-01-15T18:32:15Z",
              "createdAt": "2024-01-15T18:32:00Z"
            }
            """;
    }

    // Delivery Service Examples
    public static class Delivery {
        public static final String DELIVERY_REQUEST = """
            {
              "orderId": "60f7b1b3e4b0c8a2d8e9f0a4",
              "pickupAddress": {
                "street": "456 Food Court Ave",
                "city": "New York",
                "state": "NY",
                "zipCode": "10002",
                "country": "USA",
                "latitude": 40.7128,
                "longitude": -74.0060
              },
              "deliveryAddress": {
                "street": "123 Main St",
                "city": "New York",
                "state": "NY",
                "zipCode": "10001",
                "country": "USA",
                "latitude": 40.7589,
                "longitude": -73.9851,
                "instructions": "Ring doorbell twice"
              },
              "estimatedDistance": 2.5,
              "priority": "STANDARD",
              "specialInstructions": "Handle with care - contains liquids"
            }
            """;

        public static final String DELIVERY_RESPONSE = """
            {
              "id": "60f7b1b3e4b0c8a2d8e9f0a6",
              "orderId": "60f7b1b3e4b0c8a2d8e9f0a4",
              "deliveryPartnerId": "60f7b1b3e4b0c8a2d8e9f0a7",
              "status": "ASSIGNED",
              "estimatedDeliveryTime": "2024-01-15T19:45:00Z",
              "actualDistance": 2.3,
              "deliveryFee": 3.99,
              "trackingUrl": "https://track.mergeeats.com/delivery/60f7b1b3e4b0c8a2d8e9f0a6",
              "createdAt": "2024-01-15T18:35:00Z",
              "assignedAt": "2024-01-15T18:36:00Z"
            }
            """;
    }

    // Notification Service Examples
    public static class Notification {
        public static final String NOTIFICATION_REQUEST = """
            {
              "userId": "60f7b1b3e4b0c8a2d8e9f0a1",
              "type": "ORDER_UPDATE",
              "channel": "PUSH",
              "title": "Order Confirmed!",
              "message": "Your order #ME-2024-001234 has been confirmed by Spice Garden Indian Cuisine",
              "data": {
                "orderId": "60f7b1b3e4b0c8a2d8e9f0a4",
                "orderNumber": "ME-2024-001234",
                "restaurantName": "Spice Garden Indian Cuisine",
                "estimatedDeliveryTime": "2024-01-15T19:45:00Z"
              },
              "priority": "HIGH",
              "scheduledAt": "2024-01-15T18:32:00Z"
            }
            """;

        public static final String NOTIFICATION_RESPONSE = """
            {
              "id": "60f7b1b3e4b0c8a2d8e9f0a8",
              "userId": "60f7b1b3e4b0c8a2d8e9f0a1",
              "type": "ORDER_UPDATE",
              "channel": "PUSH",
              "status": "SENT",
              "title": "Order Confirmed!",
              "message": "Your order #ME-2024-001234 has been confirmed",
              "sentAt": "2024-01-15T18:32:05Z",
              "deliveryStatus": {
                "delivered": true,
                "deliveredAt": "2024-01-15T18:32:06Z",
                "readAt": "2024-01-15T18:33:00Z"
              },
              "createdAt": "2024-01-15T18:32:00Z"
            }
            """;
    }

    // Error Response Examples
    public static class Errors {
        public static final String VALIDATION_ERROR = """
            {
              "message": "Validation failed",
              "errors": [
                "Email must be valid",
                "Password must be at least 8 characters",
                "Phone number format is invalid"
              ],
              "timestamp": "2024-01-15T18:32:00Z",
              "path": "/api/users/register"
            }
            """;

        public static final String AUTHENTICATION_ERROR = """
            {
              "message": "Authentication failed",
              "errors": ["Invalid email or password"],
              "timestamp": "2024-01-15T18:32:00Z",
              "path": "/api/users/login"
            }
            """;

        public static final String AUTHORIZATION_ERROR = """
            {
              "message": "Access denied",
              "errors": ["Insufficient permissions to access this resource"],
              "timestamp": "2024-01-15T18:32:00Z",
              "path": "/api/admin/users"
            }
            """;

        public static final String NOT_FOUND_ERROR = """
            {
              "message": "Resource not found",
              "errors": ["User with ID 60f7b1b3e4b0c8a2d8e9f0a1 not found"],
              "timestamp": "2024-01-15T18:32:00Z",
              "path": "/api/users/60f7b1b3e4b0c8a2d8e9f0a1"
            }
            """;

        public static final String SERVER_ERROR = """
            {
              "message": "Internal server error",
              "errors": ["An unexpected error occurred. Please try again later."],
              "timestamp": "2024-01-15T18:32:00Z",
              "path": "/api/orders",
              "errorId": "ERR-2024-001234"
            }
            """;
    }

    // Common Response Patterns
    public static class Common {
        public static final String SUCCESS_RESPONSE = """
            {
              "message": "Operation completed successfully",
              "timestamp": "2024-01-15T18:32:00Z"
            }
            """;

        public static final String HEALTH_CHECK_RESPONSE = """
            {
              "status": "UP",
              "timestamp": "2024-01-15T18:32:00Z",
              "version": "2.0.0",
              "environment": "development",
              "uptime": "2d 5h 30m",
              "dependencies": {
                "database": "UP",
                "redis": "UP",
                "kafka": "UP"
              }
            }
            """;
    }
}