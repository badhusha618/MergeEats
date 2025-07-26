# Swagger Mock Test Data Integration Summary

## Overview
This document summarizes the comprehensive mock test data that has been integrated into all backend services for Swagger API testing. The integration provides realistic sample data for all API endpoints, making the Swagger UI more interactive and useful for testing.

## Services Covered

### 1. User Service
**Location**: `mergeeats/backend/user-service/`

#### DTOs with Mock Data:
- **UserRegistrationRequest.java**
  - `email`: "jane.doe@example.com"
  - `password`: "StrongPassword123!"
  - `fullName`: "Jane Doe"
  - `phoneNumber`: "+1-555-123-4567"
  - `roles`: "[\"CUSTOMER\"]"

- **LoginRequest.java**
  - `email`: "jane.doe@example.com"
  - `password`: "StrongPassword123!"

- **AuthResponse.java**
  - `token`: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  - `tokenType`: "Bearer"
  - `message`: "Login successful"

#### Controller Methods with Mock Examples:
- **POST /register** - User registration with comprehensive examples
- **POST /login** - User authentication with detailed response examples
- **GET /profile** - User profile retrieval with authentication examples

### 2. Restaurant Service
**Location**: `mergeeats/backend/restaurant-service/`

#### DTOs with Mock Data:
- **CreateRestaurantRequest.java**
  - `name`: "Pizza Palace"
  - `description`: "Authentic Italian pizza and pasta"
  - `cuisineType`: "Italian"
  - `address`: "123 Main St, City, State 12345"
  - `latitude`: 40.7128
  - `longitude`: -74.0060
  - `phoneNumber`: "+1-555-123-4567"
  - `email`: "info@pizzapalace.com"
  - `website`: "https://www.pizzapalace.com"

- **MenuItemRequest.java**
  - `name`: "Margherita Pizza"
  - `description`: "Classic tomato sauce with mozzarella cheese"
  - `price`: 12.99
  - `category`: "Pizza"
  - `isAvailable`: true
  - `isVegetarian`: false
  - `isVegan`: false
  - `isGlutenFree`: false

#### Controller Methods with Mock Examples:
- **POST /api/restaurants** - Restaurant creation with detailed examples
- **GET /api/restaurants/{restaurantId}** - Restaurant retrieval with comprehensive data
- **POST /api/restaurants/{restaurantId}/menu** - Menu item addition
- **PUT /api/restaurants/{restaurantId}/menu/{itemId}** - Menu item updates
- **GET /api/restaurants/search** - Restaurant search functionality
- **GET /api/restaurants/nearby** - Location-based restaurant discovery

### 3. Order Service
**Location**: `mergeeats/backend/order-service/`

#### DTOs with Mock Data:
- **CreateOrderRequest.java**
  - `userId`: "user_987654321"
  - `restaurantId`: "restaurant_123456789"
  - `deliveryAddress`: "123 Main St, City, State 12345"
  - `specialInstructions`: "Please deliver to the front door"
  - `paymentMethod`: "CREDIT_CARD"
  - `items`: "[{\"menuItemId\":\"item_123\",\"quantity\":2,\"specialInstructions\":\"Extra cheese please\"}]"

#### Controller Methods with Mock Examples:
- **POST /** - Order creation with AI-powered merging examples
- **GET /{orderId}** - Order retrieval with comprehensive order data
- **PUT /{orderId}/status** - Order status updates
- **POST /{orderId}/cancel** - Order cancellation with reasons
- **POST /group** - Group order creation
- **GET /statistics** - Order analytics and statistics

### 4. Payment Service
**Location**: `mergeeats/backend/payment-service/`

#### DTOs with Mock Data:
- **CreatePaymentRequest.java**
  - `orderId`: "order_123456789"
  - `userId`: "user_987654321"
  - `amount`: 29.99
  - `paymentMethod`: "CREDIT_CARD"
  - `paymentType`: "ONLINE"

- **RefundRequest.java**
  - `paymentId`: "payment_123456789"
  - `amount`: 29.99
  - `reason`: "Customer requested refund due to wrong order"

#### Controller Methods with Mock Examples:
- **POST /api/payments** - Payment creation with transaction examples
- **POST /api/payments/{paymentId}/process** - Payment processing with status updates
- **POST /api/payments/{paymentId}/refund** - Refund processing with detailed examples
- **GET /api/payments/{paymentId}** - Payment retrieval
- **GET /api/payments/order/{orderId}** - Order payment history
- **GET /api/payments/user/{userId}** - User payment history

### 5. Delivery Service
**Location**: `mergeeats/backend/delivery-service/`

#### DTOs with Mock Data:
- **CreateDeliveryRequest.java**
  - `orderId`: "order_123456789"
  - `customerId`: "user_987654321"
  - `restaurantId`: "restaurant_123456789"
  - `pickupAddress`: "123 Main St, City, State 12345"
  - `deliveryAddress`: "456 Oak Ave, City, State 12345"
  - `pickupLatitude`: 40.7128
  - `pickupLongitude`: -74.0060
  - `deliveryLatitude`: 40.7589
  - `deliveryLongitude`: -73.9851
  - `orderTotal`: 29.99
  - `deliveryFee`: 2.99
  - `scheduledPickupTime`: "2024-01-15T11:30:00Z"
  - `estimatedDeliveryTime`: "2024-01-15T12:30:00Z"
  - `deliveryInstructions`: "Please deliver to the front door"

- **UpdateLocationRequest.java**
  - `latitude`: 40.7589
  - `longitude`: -73.9851

#### Controller Methods with Mock Examples:
- **POST /deliveries** - Delivery creation with location data
- **GET /deliveries/{deliveryId}** - Delivery retrieval with tracking info
- **POST /deliveries/{deliveryId}/assign** - Delivery partner assignment
- **PUT /deliveries/{deliveryId}/status** - Delivery status updates
- **PUT /deliveries/{deliveryId}/location** - Real-time location updates
- **GET /deliveries/{deliveryId}/location** - Current delivery location
- **GET /deliveries/partner/{partnerId}/statistics** - Partner delivery analytics

### 6. Notification Service
**Location**: `mergeeats/backend/notification-service/`

#### DTOs with Mock Data:
- **CreateNotificationRequest.java**
  - `recipientId`: "user_987654321"
  - `type`: "ORDER_STATUS_UPDATE"
  - `title`: "Your order #12345 has been confirmed and is being prepared!"
  - `message`: "Your order from Pizza Palace is being prepared and will be ready for delivery soon."
  - `metadata`: "{\"orderId\":\"order_123\",\"status\":\"PREPARING\"}"

#### Controller Methods with Mock Examples:
- **POST /api/notifications** - Notification creation with multi-channel support
- **GET /api/notifications/{notificationId}** - Notification retrieval
- **GET /api/notifications/user/{userId}** - User notification history
- **PUT /api/notifications/{notificationId}/read** - Mark notification as read
- **GET /api/notifications/user/{userId}/unread** - Unread notifications
- **PUT /api/notifications/user/{userId}/read-all** - Mark all as read

## Key Features of Mock Data Integration

### 1. Realistic Data Values
- All mock data uses realistic values that represent actual business scenarios
- Consistent ID formats (e.g., "user_987654321", "order_123456789")
- Realistic coordinates, addresses, and contact information
- Proper date/time formats for scheduling and tracking

### 2. Comprehensive Coverage
- All major DTOs across all services have mock examples
- All controller endpoints include detailed request/response examples
- Error scenarios and validation examples included
- Success and failure response examples provided

### 3. Swagger UI Enhancement
- Interactive "Try it out" functionality with pre-filled data
- Detailed API documentation with examples
- Clear parameter descriptions and constraints
- Comprehensive response schemas and examples

### 4. Business Logic Representation
- Mock data reflects actual business workflows
- Order lifecycle examples (pending → preparing → delivered)
- Payment processing examples (pending → completed → refunded)
- Delivery tracking examples with location updates
- Notification examples for different scenarios

## Usage Instructions

### For Developers:
1. Access Swagger UI for any service at: `http://localhost:{port}/swagger-ui.html`
2. Use the "Try it out" button on any endpoint
3. Modify the pre-filled mock data as needed
4. Execute the API call to test functionality

### For API Testing:
1. Use the provided mock data as test cases
2. Validate responses against the expected schemas
3. Test error scenarios with invalid data
4. Verify business logic with realistic scenarios

### For Documentation:
1. Mock examples serve as API documentation
2. Clear understanding of expected request/response formats
3. Business rules and constraints clearly demonstrated
4. Integration patterns between services illustrated

## Benefits

1. **Improved Developer Experience**: Pre-filled forms make API testing easier
2. **Better Documentation**: Real examples help understand API usage
3. **Faster Testing**: No need to create test data from scratch
4. **Consistent Testing**: Standardized test scenarios across all services
5. **Business Understanding**: Mock data reflects real-world usage patterns

## Maintenance

- Mock data should be updated when business rules change
- New endpoints should include appropriate mock examples
- Data consistency should be maintained across services
- Regular review and updates recommended

---

**Note**: This mock data integration provides a comprehensive foundation for API testing and documentation. All examples are designed to be realistic and representative of actual business scenarios in the MergeEats food delivery platform. 