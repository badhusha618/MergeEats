# 🍕 MergeEats - Food Delivery Platform

A comprehensive food delivery platform built with microservices architecture, featuring customer mobile app, merchant web app, and delivery partner app.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Development Status](#development-status)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

MergeEats is a full-stack food delivery platform that connects customers, restaurants, and delivery partners. The platform features real-time order tracking, payment processing, and comprehensive analytics.

### Key Components:
- **Customer Mobile App**: React-based web app for ordering food
- **Merchant Web App**: Dashboard for restaurant owners to manage orders and menu
- **Delivery Partner App**: Mobile app for delivery partners to accept and track deliveries
- **Backend Microservices**: Spring Boot services for different business domains
- **API Gateway**: Centralized routing and authentication

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Customer      │    │    Merchant     │    │   Delivery      │
│   Mobile App    │    │   Web App       │    │  Partner App    │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────┴─────────────┐
                    │      API Gateway          │
                    │      (Port: 8080)         │
                    └─────────────┬─────────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        │                         │                         │
┌───────▼────────┐    ┌──────────▼──────────┐    ┌────────▼────────┐
│   User Service │    │  Restaurant Service │    │  Order Service  │
│   (Port: 8081) │    │   (Port: 8083)      │    │  (Port: 8082)   │
└────────────────┘    └─────────────────────┘    └─────────────────┘
        │                         │                         │
┌───────▼────────┐    ┌──────────▼──────────┐    ┌────────▼────────┐
│ Payment Service│    │ Delivery Service    │    │Notification Svc │
│ (Port: 8084)   │    │ (Port: 8085)        │    │ (Port: 8086)    │
└────────────────┘    └─────────────────────┘    └─────────────────┘
```

## ✨ Features

### Customer Features
- 🔍 **Restaurant Discovery**: Browse restaurants by cuisine, location, and ratings
- 🛒 **Smart Cart**: Add items from multiple restaurants with intelligent cart management
- 💳 **Secure Payments**: Multiple payment methods with secure processing
- 📍 **Real-time Tracking**: Live order tracking with delivery partner location
- 📱 **Responsive Design**: Mobile-first design for seamless experience
- 🔔 **Push Notifications**: Real-time updates on order status

### Merchant Features
- 📊 **Dashboard Analytics**: Real-time sales, orders, and performance metrics
- 🍽️ **Menu Management**: Add, edit, and manage menu items with categories
- 📦 **Order Management**: Accept, reject, and track order status
- 💰 **Revenue Tracking**: Detailed financial reports and earnings
- ⏰ **Operating Hours**: Set and manage restaurant availability
- 📈 **Performance Insights**: Customer feedback and ratings analysis

### Delivery Partner Features
- 🚚 **Delivery Requests**: Accept and manage delivery assignments
- 📍 **Route Optimization**: Smart routing for efficient deliveries
- 💰 **Earnings Tracking**: Real-time earnings and performance metrics
- 📱 **Status Updates**: Update delivery status and customer communication
- 🗺️ **Location Tracking**: GPS-based location sharing
- 📊 **Performance Analytics**: Delivery statistics and ratings

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: MongoDB
- **Message Broker**: Apache Kafka
- **API Gateway**: Spring Cloud Gateway
- **Authentication**: JWT
- **Documentation**: OpenAPI 3.0 (Swagger)

### Frontend
- **Framework**: React 19.x with TypeScript
- **UI Library**: Material-UI (MUI) v7
- **State Management**: React Context API
- **Routing**: React Router v6
- **HTTP Client**: Axios
- **Real-time**: Socket.io Client

### Infrastructure
- **Containerization**: Docker
- **Orchestration**: Docker Compose
- **Database**: MongoDB Atlas (Cloud)
- **Message Queue**: Apache Kafka
- **Monitoring**: Spring Boot Actuator

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17** or higher
- **Node.js 18** or higher
- **npm** or **yarn**
- **MongoDB** (local or cloud)
- **Apache Kafka** (local or cloud)
- **Docker** and **Docker Compose** (optional)

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/mergeeats.git
cd mergeeats
```

### 2. Backend Setup

```bash
# Navigate to backend directory
cd mergeeats/backend

# Build all services
mvn clean install

# Start services using Docker Compose (recommended)
docker-compose up -d

# Or start services individually
mvn spring-boot:run -pl user-service
mvn spring-boot:run -pl order-service
mvn spring-boot:run -pl restaurant-service
mvn spring-boot:run -pl payment-service
mvn spring-boot:run -pl delivery-service
mvn spring-boot:run -pl notification-service
mvn spring-boot:run -pl api-gateway
```

### 3. Frontend Setup

```bash
# Navigate to frontend directory
cd mergeeats/frontend

# Install dependencies for all apps
cd customer-mobile-app && npm install
cd ../merchant-web-app && npm install
cd ../delivery-partner-app && npm install
```

## 🏃‍♂️ Running the Application

### Backend Services

| Service | Port | Description |
|---------|------|-------------|
| API Gateway | 8080 | Centralized routing and authentication |
| User Service | 8081 | User management and authentication |
| Order Service | 8082 | Order processing and management |
| Restaurant Service | 8083 | Restaurant and menu management |
| Payment Service | 8084 | Payment processing |
| Delivery Service | 8085 | Delivery management |
| Notification Service | 8086 | Notifications and messaging |

### Frontend Applications

```bash
# Customer Mobile App
cd mergeeats/frontend/customer-mobile-app
npm start
# Access at: http://localhost:3000

# Merchant Web App
cd mergeeats/frontend/merchant-web-app
npm start
# Access at: http://localhost:3001

# Delivery Partner App
cd mergeeats/frontend/delivery-partner-app
npm start
# Access at: http://localhost:3002
```

### Environment Variables

Create `.env` files in each service directory:

```env
# Database
MONGODB_URI=mongodb://localhost:27017/mergeeats
MONGODB_USERNAME=admin
MONGODB_PASSWORD=password123

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# JWT
JWT_SECRET=your-super-secret-jwt-key

# External Services
STRIPE_SECRET_KEY=sk_test_your_stripe_key
RAZORPAY_KEY_SECRET=your_razorpay_secret
```

## 📚 API Documentation

### Swagger UI Endpoints

- **API Gateway**: http://localhost:8080/swagger-ui.html
- **User Service**: http://localhost:8081/swagger-ui.html
- **Order Service**: http://localhost:8082/swagger-ui.html
- **Restaurant Service**: http://localhost:8083/swagger-ui.html
- **Payment Service**: http://localhost:8084/swagger-ui.html
- **Delivery Service**: http://localhost:8085/swagger-ui.html
- **Notification Service**: http://localhost:8086/swagger-ui.html

### Key API Endpoints

#### Authentication
```http
POST /api/users/register
POST /api/users/login
POST /api/merchants/login
POST /api/delivery-partners/login
```

#### Restaurants
```http
GET /api/restaurants
GET /api/restaurants/{id}
GET /api/restaurants/search
GET /api/restaurants/nearby
```

#### Orders
```http
POST /api/orders
GET /api/orders/{id}
PUT /api/orders/{id}/status
GET /api/orders/{id}/track
```

#### Payments
```http
POST /api/payments
GET /api/payments/{id}
POST /api/payments/{id}/refund
```

## 📁 Project Structure

```
mergeeats/
├── backend/                          # Backend microservices
│   ├── api-gateway/                  # API Gateway service
│   ├── user-service/                 # User management service
│   ├── order-service/                # Order processing service
│   ├── restaurant-service/           # Restaurant management service
│   ├── payment-service/              # Payment processing service
│   ├── delivery-service/             # Delivery management service
│   ├── notification-service/         # Notification service
│   └── shared/                       # Shared components
│       └── common-models/            # Common data models
├── frontend/                         # Frontend applications
│   ├── customer-mobile-app/          # Customer web application
│   ├── merchant-web-app/             # Merchant dashboard
│   └── delivery-partner-app/         # Delivery partner app
├── infrastructure/                   # Infrastructure configuration
│   ├── docker-compose.yml           # Docker Compose configuration
│   └── scripts/                     # Deployment scripts
├── docs/                            # Documentation
└── README.md                        # This file
```

## 🔧 Development

### Code Style

- **Backend**: Follow Google Java Style Guide
- **Frontend**: Use Prettier and ESLint
- **Commit Messages**: Follow Conventional Commits

### Testing

```bash
# Backend tests
mvn test

# Frontend tests
npm test

# E2E tests
npm run test:e2e
```

### Database Migrations

```bash
# Run migrations
mvn flyway:migrate

# Rollback migrations
mvn flyway:rollback
```

## 🚀 Deployment

### Docker Deployment

```bash
# Build and run with Docker Compose
docker-compose up -d

# Scale services
docker-compose up -d --scale order-service=3
```

### Kubernetes Deployment

```bash
# Apply Kubernetes manifests
kubectl apply -f k8s/

# Check deployment status
kubectl get pods
kubectl get services
```

## 📊 Monitoring

### Health Checks

- **Actuator Endpoints**: `/actuator/health`
- **Custom Health Checks**: `/actuator/health/custom`

### Metrics

- **Prometheus Metrics**: `/actuator/prometheus`
- **Application Metrics**: Custom business metrics

### Logging

- **Centralized Logging**: ELK Stack integration
- **Log Levels**: Configurable per service

## 📊 Development Status

### Current Implementation Status
- **Backend Services**: 87.5% complete (7/8 services fully operational)
- **Frontend Applications**: 33% complete (Merchant dashboard ready)
- **AI Features**: 100% complete (Order merging, smart routing)
- **Infrastructure**: 100% complete (API Gateway, databases, messaging)

### 📋 Detailed Feature Tracking
For a comprehensive list of completed features and remaining to-do items, see:
- **[FEATURES_TODO.md](FEATURES_TODO.md)** - Complete feature checklist and roadmap
- **[FINAL_STATUS.md](FINAL_STATUS.md)** - Current implementation status
- **[IMPLEMENTATION_COMPLETE.md](IMPLEMENTATION_COMPLETE.md)** - Detailed completion report

### 🎯 Immediate Priorities
1. Fix Payment and Notification service compilation issues
2. Complete Customer mobile app implementation
3. Complete Delivery Partner app implementation
4. Add real-time tracking UI components

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Write unit tests for new features
- Update documentation for API changes
- Follow the existing code style
- Add appropriate error handling
- Include logging for debugging
- Update FEATURES_TODO.md when completing features

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:

- 📧 Email: support@mergeeats.com
- 💬 Discord: [MergeEats Community](https://discord.gg/mergeeats)
- 📖 Documentation: [docs.mergeeats.com](https://docs.mergeeats.com)
- 🐛 Issues: [GitHub Issues](https://github.com/yourusername/mergeeats/issues)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Material-UI team for the beautiful components
- MongoDB team for the flexible database
- Apache Kafka team for the reliable messaging system

---

**Made with ❤️ by the Shahul Hameed Badhusha**
