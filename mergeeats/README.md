# MergeEats - AI-Powered Delivery and Group Ordering Platform

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2.0-blue.svg)](https://reactjs.org/)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-7.4.0-black.svg)](https://kafka.apache.org/)

## 🚀 Overview

MergeEats is a comprehensive, AI-powered food delivery platform that revolutionizes the way people order food through intelligent order merging, dynamic routing, and group ordering capabilities. Built with a modern microservices architecture, it provides seamless experiences for customers, restaurants, and delivery partners.

### ✨ Key Features

- **🤖 AI-Powered Order Merging**: Intelligent algorithms combine multiple orders for faster delivery
- **🚗 Dynamic Delivery Routing**: Real-time route optimization for delivery partners
- **👥 Group Ordering**: Collaborative ordering with automatic split payments
- **📍 Live Delivery Tracking**: Real-time tracking with WebSocket updates
- **💡 AI Recommendations**: Personalized food recommendations
- **📊 Demand Forecasting**: AI-driven inventory and demand prediction
- **🔄 Batch Delivery Management**: Efficient delivery partner assignment
- **💳 Secure Payments**: Integrated payment processing with Stripe/Razorpay
- **📱 Multi-Platform**: Web and mobile applications

## 🏗️ Architecture

### Microservices Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │    │  Config Server  │    │   Discovery     │
│    (Port 8080)  │    │   (Port 8888)   │    │    Service      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ├───────────────────────┼───────────────────────┤
         │                       │                       │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  User Service   │    │ Order Service   │    │Restaurant Service│
│   (Port 8081)   │    │  (Port 8082)    │    │   (Port 8083)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│Delivery Service │    │Payment Service  │    │Notification Svc │
│   (Port 8084)   │    │  (Port 8085)    │    │   (Port 8086)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Frontend Applications

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Merchant Web    │    │ Customer Mobile │    │Delivery Partner │
│ App (React.js)  │    │ App (React Native)   │ App (React Native)
│   (Port 3000)   │    │   (Port 3001)   │    │   (Port 3002)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Infrastructure Components

- **MongoDB**: Primary database for all services
- **Apache Kafka**: Event streaming and service communication
- **Redis**: Caching and session management
- **Docker**: Containerization and orchestration

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: MongoDB 7.0
- **Message Broker**: Apache Kafka 7.4.0
- **Cache**: Redis 7.2
- **API Gateway**: Spring Cloud Gateway
- **Security**: JWT Authentication
- **Documentation**: OpenAPI 3.0 (Swagger)

### Frontend
- **Web**: React.js 18 with TypeScript
- **Mobile**: React Native
- **State Management**: Redux Toolkit
- **UI Components**: Material-UI / Ant Design
- **Real-time**: Socket.io

### DevOps & Infrastructure
- **Containerization**: Docker & Docker Compose
- **Orchestration**: Kubernetes (optional)
- **Monitoring**: Spring Boot Actuator
- **API Documentation**: Swagger UI

## 📋 Prerequisites

Before running MergeEats, ensure you have the following installed:

- **Java 17** or higher
- **Node.js 18** or higher
- **npm** or **yarn**
- **Docker** and **Docker Compose**
- **Maven 3.8** or higher
- **Git**

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/mergeeats.git
cd mergeeats
```

### 2. Start Infrastructure Services

```bash
# Start MongoDB, Kafka, Redis, and other infrastructure
cd infrastructure/docker-compose
docker-compose up -d mongodb kafka redis zookeeper
```

### 3. Build and Run Backend Services

```bash
# Build all services
mvn clean install

# Start services in order
cd backend/config-server && mvn spring-boot:run &
sleep 30
cd ../api-gateway && mvn spring-boot:run &
cd ../user-service && mvn spring-boot:run &
cd ../order-service && mvn spring-boot:run &
cd ../restaurant-service && mvn spring-boot:run &
cd ../delivery-service && mvn spring-boot:run &
cd ../payment-service && mvn spring-boot:run &
cd ../notification-service && mvn spring-boot:run &
```

### 4. Start Frontend Applications

```bash
# Merchant Web App
cd frontend/merchant-web-app
npm install
npm start

# In separate terminals for other apps
cd frontend/customer-mobile-app
npm install
npm start

cd frontend/delivery-partner-app
npm install
npm start
```

### 5. Access Applications

- **API Gateway**: http://localhost:8080
- **Merchant Web App**: http://localhost:3000
- **Customer App**: http://localhost:3001
- **Delivery Partner App**: http://localhost:3002
- **API Documentation**: http://localhost:8080/swagger-ui.html

## 🐳 Docker Deployment

For a complete containerized deployment:

```bash
# Start all services with Docker Compose
cd infrastructure/docker-compose
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f [service-name]
```

## 📚 Service Documentation

### User Service (Port 8081)
- User registration and authentication
- Profile management
- JWT token generation
- Role-based access control

**Key Endpoints:**
- `POST /api/users/register` - User registration
- `POST /api/users/login` - User authentication
- `GET /api/users/{userId}` - Get user profile
- `PUT /api/users/{userId}` - Update user profile

### Order Service (Port 8082)
- Order creation and management
- AI-powered order merging
- Group ordering functionality
- Order status tracking

**Key Endpoints:**
- `POST /api/orders` - Create new order
- `GET /api/orders/{orderId}` - Get order details
- `PUT /api/orders/{orderId}/status` - Update order status
- `POST /api/orders/group` - Create group order

### Restaurant Service (Port 8083)
- Restaurant profile management
- Menu management
- Inventory tracking
- Demand forecasting

**Key Endpoints:**
- `POST /api/restaurants` - Register restaurant
- `GET /api/restaurants/{restaurantId}` - Get restaurant details
- `POST /api/restaurants/{restaurantId}/menu` - Add menu items
- `GET /api/restaurants/search` - Search restaurants

### Delivery Service (Port 8084)
- Delivery partner management
- Route optimization
- Real-time tracking
- Batch delivery assignments

**Key Endpoints:**
- `POST /api/delivery/assign` - Assign delivery
- `GET /api/delivery/{deliveryId}/track` - Track delivery
- `PUT /api/delivery/{deliveryId}/status` - Update delivery status
- `GET /api/delivery/routes/optimize` - Optimize routes

### Payment Service (Port 8085)
- Payment processing
- Split payment handling
- Refund management
- Payment method storage

**Key Endpoints:**
- `POST /api/payments/process` - Process payment
- `POST /api/payments/split` - Handle split payments
- `POST /api/payments/refund` - Process refunds
- `GET /api/payments/{paymentId}` - Get payment details

### Notification Service (Port 8086)
- Real-time notifications
- Email/SMS notifications
- Push notifications
- Notification preferences

**Key Endpoints:**
- `POST /api/notifications/send` - Send notification
- `GET /api/notifications/{userId}` - Get user notifications
- `PUT /api/notifications/preferences` - Update preferences
- `WebSocket: /ws/notifications` - Real-time notifications

## 🧪 Testing

### Running Tests

```bash
# Run all backend tests
mvn test

# Run specific service tests
cd backend/user-service
mvn test

# Run frontend tests
cd frontend/merchant-web-app
npm test
```

### API Testing with Postman

Import the Postman collection from `docs/api-specs/MergeEats.postman_collection.json`

## 📊 Monitoring and Health Checks

### Health Check Endpoints

- **User Service**: http://localhost:8081/actuator/health
- **Order Service**: http://localhost:8082/actuator/health
- **Restaurant Service**: http://localhost:8083/actuator/health
- **Delivery Service**: http://localhost:8084/actuator/health
- **Payment Service**: http://localhost:8085/actuator/health
- **Notification Service**: http://localhost:8086/actuator/health

### Metrics

Access metrics at `/actuator/metrics` for each service.

## 🔧 Configuration

### Environment Variables

Create `.env` files for each service:

```bash
# User Service
MONGODB_URI=mongodb://localhost:27017/mergeeats-users
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
JWT_SECRET=your-jwt-secret
JWT_EXPIRATION=86400000

# Order Service
MONGODB_URI=mongodb://localhost:27017/mergeeats-orders
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Payment Service
STRIPE_API_KEY=your-stripe-api-key
RAZORPAY_API_KEY=your-razorpay-api-key
```

### Database Configuration

MongoDB collections are automatically created with proper indexing:

- `users` - User profiles and authentication
- `orders` - Order management and tracking
- `restaurants` - Restaurant and menu data
- `deliveries` - Delivery tracking and management
- `payments` - Payment transactions
- `notifications` - Notification history

## 🤝 Contributing

We welcome contributions to MergeEats! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Coding Standards

- Follow Java coding conventions for backend
- Use ESLint and Prettier for frontend code
- Write comprehensive tests for new features
- Update documentation for API changes

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

If you encounter any issues or have questions:

1. Check the [FAQ](docs/FAQ.md)
2. Search [existing issues](https://github.com/your-username/mergeeats/issues)
3. Create a [new issue](https://github.com/your-username/mergeeats/issues/new)
4. Join our [Discord community](https://discord.gg/mergeeats)

## 🎯 Roadmap

### Phase 1 (Current)
- ✅ Core microservices architecture
- ✅ User authentication and management
- ✅ Basic order management
- ✅ Restaurant onboarding
- ✅ Payment integration

### Phase 2 (Next Quarter)
- 🔄 AI-powered order merging
- 🔄 Advanced route optimization
- 🔄 Real-time tracking
- 🔄 Group ordering features
- 🔄 Mobile applications

### Phase 3 (Future)
- 📱 Advanced mobile features
- 🤖 Machine learning recommendations
- 📊 Advanced analytics dashboard
- 🌍 Multi-region support
- 🔌 Third-party integrations

## 👥 Team

- **Backend Development**: Spring Boot microservices
- **Frontend Development**: React.js and React Native
- **DevOps**: Docker, Kubernetes, CI/CD
- **AI/ML**: Order optimization and recommendations
- **QA**: Automated testing and quality assurance

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React community for the frontend ecosystem
- MongoDB for the flexible database solution
- Apache Kafka for reliable message streaming
- Docker for containerization support

---

**MergeEats** - Revolutionizing food delivery through AI and intelligent systems.

For more information, visit our [documentation](docs/) or contact us at support@mergeeats.com.