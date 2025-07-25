#!/bin/bash

# MergeEats Setup Script
# This script sets up the complete MergeEats development environment

set -e

echo "🚀 Setting up MergeEats Development Environment"
echo "================================================"

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

# Check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    # Check Java
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed. Please install Java 17 or higher."
        exit 1
    fi
    
    java_version=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
    if [ "$java_version" -lt 17 ]; then
        print_error "Java 17 or higher is required. Current version: $java_version"
        exit 1
    fi
    print_success "Java $java_version found"
    
    # Check Maven
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed. Please install Maven 3.8 or higher."
        exit 1
    fi
    print_success "Maven found"
    
    # Check Node.js
    if ! command -v node &> /dev/null; then
        print_error "Node.js is not installed. Please install Node.js 18 or higher."
        exit 1
    fi
    
    node_version=$(node -v | sed 's/v//' | cut -d'.' -f1)
    if [ "$node_version" -lt 18 ]; then
        print_error "Node.js 18 or higher is required. Current version: $node_version"
        exit 1
    fi
    print_success "Node.js $node_version found"
    
    # Check npm
    if ! command -v npm &> /dev/null; then
        print_error "npm is not installed."
        exit 1
    fi
    print_success "npm found"
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker."
        exit 1
    fi
    print_success "Docker found"
    
    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed."
        exit 1
    fi
    print_success "Docker Compose found"
}

# Setup infrastructure
setup_infrastructure() {
    print_status "Setting up infrastructure services..."
    
    cd infrastructure/docker-compose
    
    print_status "Starting MongoDB, Kafka, Redis, and Zookeeper..."
    docker-compose up -d mongodb kafka redis zookeeper
    
    print_status "Waiting for services to be ready..."
    sleep 30
    
    print_success "Infrastructure services started"
    cd ../..
}

# Build backend services
build_backend() {
    print_status "Building backend services..."
    
    # Build common models first
    print_status "Building common models..."
    cd backend/shared/common-models
    mvn clean install -q
    cd ../../..
    
    # Build all services
    print_status "Building all microservices..."
    mvn clean install -q
    
    print_success "Backend services built successfully"
}

# Setup frontend
setup_frontend() {
    print_status "Setting up frontend applications..."
    
    # Merchant Web App
    print_status "Setting up Merchant Web App..."
    cd frontend/merchant-web-app
    npm install --silent
    cd ../..
    
    print_success "Frontend applications set up successfully"
}

# Create environment files
create_env_files() {
    print_status "Creating environment configuration files..."
    
    # User Service
    cat > backend/user-service/src/main/resources/application-local.yml << EOF
server:
  port: 8081

spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/mergeeats-users
  kafka:
    bootstrap-servers: localhost:9092

jwt:
  secret: mySecretKey123456789012345678901234567890
  expiration: 86400000
EOF

    # Order Service
    cat > backend/order-service/src/main/resources/application-local.yml << EOF
server:
  port: 8082

spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/mergeeats-orders
  kafka:
    bootstrap-servers: localhost:9092
EOF

    print_success "Environment files created"
}

# Start services
start_services() {
    print_status "Starting backend services..."
    
    # Create logs directory
    mkdir -p logs
    
    # Start User Service
    print_status "Starting User Service..."
    cd backend/user-service
    nohup mvn spring-boot:run -Dspring-boot.run.profiles=local > ../../logs/user-service.log 2>&1 &
    echo $! > ../../logs/user-service.pid
    cd ../..
    
    print_status "Waiting for User Service to start..."
    sleep 15
    
    print_success "Services are starting in the background"
    print_status "Check logs in the 'logs' directory"
    print_status "User Service should be available at: http://localhost:8081"
}

# Main execution
main() {
    echo "Starting MergeEats setup..."
    
    check_prerequisites
    setup_infrastructure
    build_backend
    setup_frontend
    create_env_files
    start_services
    
    echo ""
    print_success "🎉 MergeEats setup completed successfully!"
    echo ""
    echo "📋 Next Steps:"
    echo "1. Check service logs: tail -f logs/user-service.log"
    echo "2. Access User Service: http://localhost:8081/api/users/health"
    echo "3. View API Documentation: http://localhost:8081/swagger-ui.html"
    echo "4. Start frontend: cd frontend/merchant-web-app && npm start"
    echo ""
    echo "🛑 To stop services:"
    echo "   ./scripts/setup/stop.sh"
    echo ""
    echo "📚 For more information, see README.md"
}

# Execute main function
main "$@"