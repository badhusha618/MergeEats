# Getting Started with MergeEats

Welcome to MergeEats! This guide will help you get the AI-powered delivery platform up and running on your local machine.

## 🎯 Quick Start (Recommended)

The fastest way to get MergeEats running:

```bash
# Clone and setup
git clone <repository-url>
cd mergeeats

# Run the automated setup script
./scripts/setup/setup.sh
```

This script will:
- ✅ Check all prerequisites
- ✅ Start infrastructure services (MongoDB, Kafka, Redis)
- ✅ Build all backend services
- ✅ Install frontend dependencies
- ✅ Start the User Service
- ✅ Create necessary configuration files

## 📋 Manual Setup

If you prefer to set up manually or encounter issues:

### 1. Prerequisites Check

Ensure you have:
- **Java 17+**: `java -version`
- **Maven 3.8+**: `mvn -version`
- **Node.js 18+**: `node -v`
- **Docker & Docker Compose**: `docker --version && docker-compose --version`

### 2. Infrastructure Setup

```bash
cd infrastructure/docker-compose
docker-compose up -d mongodb kafka redis zookeeper
```

Wait for services to be ready (~30 seconds).

### 3. Backend Services

```bash
# Build common models first
cd backend/shared/common-models
mvn clean install

# Return to root and build all services
cd ../../..
mvn clean install

# Start User Service
cd backend/user-service
mvn spring-boot:run
```

### 4. Frontend Setup

```bash
# In a new terminal
cd frontend/merchant-web-app
npm install
npm start
```

## 🧪 Testing Your Setup

### 1. Check Infrastructure
```bash
# MongoDB
curl http://localhost:27017

# Kafka (should show broker info)
docker logs mergeeats-kafka | grep "started"
```

### 2. Test User Service
```bash
# Health check
curl http://localhost:8081/api/users/health

# API documentation
open http://localhost:8081/swagger-ui.html
```

### 3. Register a Test User
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'
```

### 4. Test Login
```bash
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

## 🎛️ Service Ports

| Service | Port | URL |
|---------|------|-----|
| User Service | 8081 | http://localhost:8081 |
| Order Service | 8082 | http://localhost:8082 |
| Restaurant Service | 8083 | http://localhost:8083 |
| Delivery Service | 8084 | http://localhost:8084 |
| Payment Service | 8085 | http://localhost:8085 |
| Notification Service | 8086 | http://localhost:8086 |
| Merchant Web App | 3000 | http://localhost:3000 |
| Customer App | 3001 | http://localhost:3001 |
| Delivery Partner App | 3002 | http://localhost:3002 |

## 🔧 Configuration

### Environment Variables

Create `.env` files for each service if needed:

```bash
# backend/user-service/.env
MONGODB_URI=mongodb://localhost:27017/mergeeats-users
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
```

### Database Access

MongoDB is accessible at:
- **Host**: localhost
- **Port**: 27017
- **Databases**: 
  - mergeeats-users
  - mergeeats-orders
  - mergeeats-restaurants
  - mergeeats-delivery
  - mergeeats-payments
  - mergeeats-notifications

## 📊 Monitoring

### Health Checks
All services expose health endpoints:
```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
# ... etc for other services
```

### Logs
Service logs are available in the `logs/` directory:
```bash
tail -f logs/user-service.log
```

### Docker Services
```bash
# Check Docker service status
docker-compose ps

# View service logs
docker-compose logs -f mongodb
docker-compose logs -f kafka
```

## 🐛 Troubleshooting

### Common Issues

#### 1. Port Already in Use
```bash
# Find process using port 8081
lsof -i :8081
# Kill the process
kill -9 <PID>
```

#### 2. MongoDB Connection Issues
```bash
# Check if MongoDB is running
docker ps | grep mongo

# Restart MongoDB
docker-compose restart mongodb
```

#### 3. Kafka Connection Issues
```bash
# Check Kafka logs
docker-compose logs kafka

# Restart Kafka and Zookeeper
docker-compose restart zookeeper kafka
```

#### 4. Build Failures
```bash
# Clean and rebuild
mvn clean install -U

# Skip tests if needed
mvn clean install -DskipTests
```

#### 5. Frontend Issues
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

### Service Dependencies

Services must start in this order:
1. Infrastructure (MongoDB, Kafka, Redis)
2. User Service (authentication required by others)
3. Other services can start in any order

### Memory Issues

If you encounter memory issues:
```bash
# Increase Java heap size
export MAVEN_OPTS="-Xmx2g"

# Or set for specific service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx1g"
```

## 🚀 Next Steps

1. **Explore APIs**: Visit http://localhost:8081/swagger-ui.html
2. **Add More Services**: Start Order Service, Restaurant Service, etc.
3. **Test Frontend**: Access http://localhost:3000
4. **Read Documentation**: Check `/docs` folder for detailed guides
5. **Join Development**: See CONTRIBUTING.md

## 🛑 Stopping Services

```bash
# Stop all services
./scripts/setup/stop.sh

# Or manually stop Docker services
cd infrastructure/docker-compose
docker-compose down
```

## 📞 Getting Help

- **Documentation**: Check the `/docs` folder
- **Issues**: Create an issue on GitHub
- **Community**: Join our Discord server
- **Email**: support@mergeeats.com

Happy coding! 🎉