#!/bin/bash

# MergeEats Complete Startup Script
# This script starts all services in the correct order

set -e

echo "🚀 Starting Complete MergeEats Platform"
echo "========================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Create logs directory
mkdir -p logs

# Function to wait for service to be ready
wait_for_service() {
    local service_name=$1
    local port=$2
    local max_attempts=30
    local attempt=1
    
    print_status "Waiting for $service_name to be ready on port $port..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:$port/actuator/health > /dev/null 2>&1; then
            print_success "$service_name is ready!"
            return 0
        fi
        
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    print_error "$service_name failed to start within expected time"
    return 1
}

# Function to start a service
start_service() {
    local service_name=$1
    local service_path=$2
    local port=$3
    
    print_status "Starting $service_name..."
    
    cd $service_path
    nohup mvn spring-boot:run > ../../logs/${service_name}.log 2>&1 &
    echo $! > ../../logs/${service_name}.pid
    cd ../..
    
    wait_for_service $service_name $port
}

# Step 1: Start Infrastructure Services
print_status "Starting infrastructure services..."
cd infrastructure/docker-compose
docker-compose up -d mongodb kafka redis zookeeper
print_success "Infrastructure services started"
cd ../..

# Wait for infrastructure to be ready
print_status "Waiting for infrastructure services to be ready..."
sleep 30

# Step 2: Build all services
print_status "Building all backend services..."
mvn clean install -q
print_success "All services built successfully"

# Step 3: Start backend services in order
print_status "Starting backend services..."

# Start User Service first (required by others for authentication)
start_service "user-service" "backend/user-service" 8081

# Start API Gateway
start_service "api-gateway" "backend/api-gateway" 8080

# Start Order Service
start_service "order-service" "backend/order-service" 8082

# Step 4: Start frontend applications
print_status "Starting frontend applications..."

# Start Merchant Web App
print_status "Starting Merchant Web App..."
cd frontend/merchant-web-app
nohup npm start > ../../logs/merchant-web-app.log 2>&1 &
echo $! > ../../logs/merchant-web-app.pid
cd ../..

# Wait for frontend to be ready
sleep 10

# Step 5: Display status
echo ""
print_success "🎉 MergeEats Platform Started Successfully!"
echo ""
echo "📋 Service Status:"
echo "=================="
echo "🔧 Infrastructure:"
echo "   - MongoDB:     http://localhost:27017"
echo "   - Kafka:       http://localhost:9092"
echo "   - Redis:       http://localhost:6379"
echo ""
echo "⚙️  Backend Services:"
echo "   - API Gateway:        http://localhost:8080"
echo "   - User Service:       http://localhost:8081"
echo "   - Order Service:      http://localhost:8082"
echo ""
echo "🎨 Frontend Applications:"
echo "   - Merchant Dashboard: http://localhost:3000"
echo ""
echo "📚 API Documentation:"
echo "   - Gateway Swagger:    http://localhost:8080/swagger-ui.html"
echo "   - User Service:       http://localhost:8081/swagger-ui.html"
echo "   - Order Service:      http://localhost:8082/swagger-ui.html"
echo ""
echo "🔍 Health Checks:"
echo "   - Gateway Health:     http://localhost:8080/actuator/health"
echo "   - User Service:       http://localhost:8081/actuator/health"
echo "   - Order Service:      http://localhost:8082/actuator/health"
echo ""
echo "📊 Monitoring:"
echo "   - Gateway Metrics:    http://localhost:8080/actuator/metrics"
echo "   - Gateway Routes:     http://localhost:8080/actuator/gateway/routes"
echo ""
echo "🗂️  Logs:"
echo "   - Check logs:         tail -f logs/[service-name].log"
echo "   - All logs:           tail -f logs/*.log"
echo ""
echo "🛑 To stop all services:"
echo "   ./scripts/setup/stop.sh"
echo ""

# Step 6: Test basic functionality
print_status "Testing basic functionality..."

# Test API Gateway
if curl -s http://localhost:8080/actuator/health > /dev/null; then
    print_success "✅ API Gateway is responding"
else
    print_warning "⚠️  API Gateway health check failed"
fi

# Test User Service through Gateway
if curl -s http://localhost:8080/api/users/health > /dev/null; then
    print_success "✅ User Service is accessible through Gateway"
else
    print_warning "⚠️  User Service not accessible through Gateway"
fi

# Test Order Service through Gateway
if curl -s http://localhost:8080/api/orders/health > /dev/null; then
    print_success "✅ Order Service is accessible through Gateway"
else
    print_warning "⚠️  Order Service not accessible through Gateway"
fi

# Test Frontend
if curl -s http://localhost:3000 > /dev/null; then
    print_success "✅ Merchant Dashboard is running"
else
    print_warning "⚠️  Merchant Dashboard not accessible"
fi

echo ""
print_success "🎊 MergeEats is ready for use!"
echo ""
echo "💡 Quick Start:"
echo "1. Open Merchant Dashboard: http://localhost:3000"
echo "2. Test API: curl http://localhost:8080/api/users/health"
echo "3. Register User: curl -X POST http://localhost:8080/api/users/register -H 'Content-Type: application/json' -d '{\"email\":\"test@example.com\",\"password\":\"password123\",\"fullName\":\"Test User\"}'"
echo ""
echo "🎯 Next Steps:"
echo "- Explore the beautiful merchant dashboard"
echo "- Test user registration and login"
echo "- Create orders and see AI-powered merging in action"
echo "- Check real-time analytics and monitoring"
echo ""
echo "Happy coding! 🚀"