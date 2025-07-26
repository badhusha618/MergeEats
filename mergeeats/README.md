# 🍕 MergeEats - Multi-Restaurant Food Delivery Platform

A comprehensive food delivery platform that allows customers to order from multiple restaurants in a single order, with AI-powered order merging for efficient delivery.

## 🚀 Live Demo

### 📱 Customer Mobile App
**Demo:** [Customer App Demo](https://customer-app-demo.mergeeats.com)
- **Features:** Multi-restaurant ordering, real-time tracking, AI-powered order merging
- **Tech Stack:** React, TypeScript, Material-UI, WebSocket

### 🏪 Merchant Web App  
**Demo:** [Merchant App Demo](https://merchant-app-demo.mergeeats.com)
- **Features:** Restaurant management, order processing, menu management, analytics
- **Tech Stack:** React, TypeScript, Material-UI, Chart.js

### 🚚 Delivery Partner App
**Demo:** [Delivery App Demo](https://delivery-app-demo.mergeeats.com)
- **Features:** Delivery management, real-time tracking, earnings dashboard
- **Tech Stack:** React, TypeScript, Material-UI, Geolocation API

## 📱 Application Previews

### Customer Mobile App Preview
![Customer App Preview](docs/previews/customer-app-preview.gif)

**Key Features:**
- 🔍 **Multi-Restaurant Search** - Find and order from multiple restaurants
- 🛒 **Smart Cart Management** - Add items from different restaurants
- 🤖 **AI Order Merging** - Automatic order optimization for efficiency
- 📍 **Real-time Tracking** - Live delivery status and location updates
- 💳 **Secure Payments** - Multiple payment methods with encryption
- ⭐ **Rating System** - Rate restaurants and delivery partners

### Merchant Web App Preview
![Merchant App Preview](docs/previews/merchant-app-preview.gif)

**Key Features:**
- 📊 **Dashboard Analytics** - Sales, orders, and performance metrics
- 🍽️ **Menu Management** - Add, edit, and organize menu items
- 📦 **Order Processing** - Real-time order notifications and status updates
- 💰 **Revenue Tracking** - Earnings, commissions, and financial reports
- 🔔 **Notification System** - Order alerts and customer communications
- 📈 **Performance Insights** - Customer feedback and business analytics

### Delivery Partner App Preview
![Delivery App Preview](docs/previews/delivery-app-preview.gif)

**Key Features:**
- 🚚 **Delivery Management** - Accept, track, and complete deliveries
- 📍 **Real-time Location** - GPS tracking and route optimization
- 💰 **Earnings Dashboard** - Track earnings, bonuses, and payments
- 📱 **Communication Tools** - Contact customers and restaurants
- 📊 **Performance Metrics** - Delivery stats and ratings
- 🔔 **Push Notifications** - Instant delivery requests and updates

## 🏗️ Architecture

### Backend Microservices
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │    │   User Service  │    │ Restaurant Svc  │
│   (Port: 8080)  │    │   (Port: 8081)  │    │   (Port: 8082)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │              ┌─────────────────┐              │
         │              │   Order Service │              │
         │              │   (Port: 8083)  │              │
         │              └─────────────────┘              │
         │                       │                       │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Payment Service │    │ Delivery Service│    │Notification Svc │
│   (Port: 8084)  │    │   (Port: 8085)  │    │   (Port: 8086)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Frontend Applications
```
┌─────────────────────────────────────────────────────────────┐
│                    Customer Mobile App                      │
│              React + TypeScript + Material-UI               │
│                    (Port: 3000)                            │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    Merchant Web App                        │
│              React + TypeScript + Material-UI               │
│                    (Port: 3001)                            │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                  Delivery Partner App                      │
│              React + TypeScript + Material-UI               │
│                    (Port: 3002)                            │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ Technology Stack

### Backend
- **Framework:** Spring Boot 3.x
- **Language:** Java 17
- **Database:** MongoDB
- **Message Broker:** Apache Kafka
- **API Documentation:** OpenAPI 3.0 (Swagger)
- **Authentication:** JWT
- **Real-time:** WebSocket
- **Build Tool:** Maven

### Frontend
- **Framework:** React 18
- **Language:** TypeScript
- **UI Library:** Material-UI (MUI)
- **State Management:** React Context API
- **HTTP Client:** Axios
- **Routing:** React Router DOM
- **Build Tool:** Create React App

### DevOps & Infrastructure
- **Containerization:** Docker
- **Orchestration:** Docker Compose
- **API Gateway:** Spring Cloud Gateway
- **Service Discovery:** Eureka (planned)
- **Monitoring:** Actuator + Prometheus (planned)

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- MongoDB 6+
- Apache Kafka 3+
- Docker & Docker Compose

### Backend Setup
```bash
# Clone the repository
git clone https://github.com/yourusername/mergeeats.git
cd mergeeats

# Start infrastructure services
docker-compose up -d mongodb kafka

# Build and start backend services
cd backend
mvn clean install
mvn spring-boot:run -pl api-gateway
mvn spring-boot:run -pl user-service
mvn spring-boot:run -pl restaurant-service
mvn spring-boot:run -pl order-service
mvn spring-boot:run -pl payment-service
mvn spring-boot:run -pl delivery-service
mvn spring-boot:run -pl notification-service
```

### Frontend Setup
```bash
# Customer Mobile App
cd frontend/customer-mobile-app
npm install
npm start

# Merchant Web App
cd frontend/merchant-web-app
npm install
npm start

# Delivery Partner App
cd frontend/delivery-partner-app
npm install
npm start
```

## 📋 Features

### 🍕 Multi-Restaurant Ordering
- Order from multiple restaurants in a single transaction
- AI-powered order merging for optimal delivery efficiency
- Smart cart management with restaurant grouping

### 🤖 AI-Powered Features
- **Order Merging:** Automatically combines orders for efficient delivery
- **Route Optimization:** Calculates optimal delivery routes
- **Demand Prediction:** Forecasts order volumes for restaurants
- **Smart Recommendations:** Personalized restaurant and menu suggestions

### 📱 Real-time Features
- Live order tracking with GPS coordinates
- Real-time delivery status updates
- WebSocket-based notifications
- Live chat between customers and delivery partners

### 🔐 Security & Authentication
- JWT-based authentication
- Role-based access control (Customer, Merchant, Delivery Partner)
- Secure payment processing
- Data encryption in transit and at rest

### 📊 Analytics & Reporting
- Comprehensive dashboard for merchants
- Delivery performance metrics
- Revenue and earnings tracking
- Customer behavior analytics

## 🗂️ Project Structure

```
mergeeats/
├── backend/                          # Backend microservices
│   ├── api-gateway/                  # API Gateway service
│   ├── user-service/                 # User management service
│   ├── restaurant-service/           # Restaurant management service
│   ├── order-service/                # Order processing service
│   ├── payment-service/              # Payment processing service
│   ├── delivery-service/             # Delivery management service
│   └── notification-service/         # Notification service
├── frontend/                         # Frontend applications
│   ├── customer-mobile-app/          # Customer mobile application
│   ├── merchant-web-app/             # Merchant web application
│   └── delivery-partner-app/         # Delivery partner application
├── docs/                             # Documentation
│   ├── api/                          # API documentation
│   ├── architecture/                 # Architecture diagrams
│   └── previews/                     # App preview videos/GIFs
├── docker-compose.yml               # Infrastructure setup
└── README.md                        # Project documentation
```

## 🔧 API Documentation

### Swagger UI Endpoints
- **API Gateway:** http://localhost:8080/swagger-ui.html
- **User Service:** http://localhost:8081/swagger-ui.html
- **Restaurant Service:** http://localhost:8082/swagger-ui.html
- **Order Service:** http://localhost:8083/swagger-ui.html
- **Payment Service:** http://localhost:8084/swagger-ui.html
- **Delivery Service:** http://localhost:8085/swagger-ui.html
- **Notification Service:** http://localhost:8086/swagger-ui.html

## 🧪 Testing

### Backend Testing
```bash
# Run all tests
mvn test

# Run specific service tests
mvn test -pl user-service
mvn test -pl restaurant-service
```

### Frontend Testing
```bash
# Customer App
cd frontend/customer-mobile-app
npm test

# Merchant App
cd frontend/merchant-web-app
npm test

# Delivery App
cd frontend/delivery-partner-app
npm test
```

## 🚀 Deployment

### Docker Deployment
```bash
# Build and run all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Production Deployment
```bash
# Build production images
docker-compose -f docker-compose.prod.yml build

# Deploy to production
docker-compose -f docker-compose.prod.yml up -d
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Backend Development:** [Your Name]
- **Frontend Development:** [Your Name]
- **DevOps & Infrastructure:** [Your Name]
- **UI/UX Design:** [Your Name]

## 📞 Support

- **Email:** support@mergeeats.com
- **Documentation:** [docs.mergeeats.com](https://docs.mergeeats.com)
- **Issues:** [GitHub Issues](https://github.com/yourusername/mergeeats/issues)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Material-UI team for the beautiful component library
- MongoDB team for the flexible database solution
- Apache Kafka team for the reliable message broker

---

⭐ **Star this repository if you find it helpful!**