#!/bin/bash

# MergeEats Stop Script
# This script stops all MergeEats services

set -e

echo "🛑 Stopping MergeEats Services"
echo "==============================="

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

# Stop Spring Boot services
stop_backend_services() {
    print_status "Stopping backend services..."
    
    # Stop services based on PID files
    for service in user-service order-service restaurant-service delivery-service payment-service notification-service; do
        if [ -f "logs/${service}.pid" ]; then
            pid=$(cat "logs/${service}.pid")
            if ps -p $pid > /dev/null 2>&1; then
                print_status "Stopping ${service} (PID: $pid)..."
                kill $pid
                rm "logs/${service}.pid"
                print_success "${service} stopped"
            else
                print_warning "${service} PID file exists but process not found"
                rm "logs/${service}.pid"
            fi
        else
            print_warning "No PID file found for ${service}"
        fi
    done
    
    # Kill any remaining Spring Boot processes
    print_status "Checking for remaining Spring Boot processes..."
    pkill -f "spring-boot:run" 2>/dev/null || true
    pkill -f "mergeeats" 2>/dev/null || true
}

# Stop Docker services
stop_infrastructure() {
    print_status "Stopping infrastructure services..."
    
    cd infrastructure/docker-compose
    
    print_status "Stopping Docker containers..."
    docker-compose down
    
    print_success "Infrastructure services stopped"
    cd ../..
}

# Stop frontend services
stop_frontend() {
    print_status "Stopping frontend services..."
    
    # Kill any Node.js processes related to our frontend
    pkill -f "react-scripts start" 2>/dev/null || true
    pkill -f "npm start" 2>/dev/null || true
    
    print_success "Frontend services stopped"
}

# Clean up logs
cleanup_logs() {
    print_status "Cleaning up logs and temporary files..."
    
    # Remove PID files
    rm -f logs/*.pid
    
    # Optionally remove log files (uncomment if desired)
    # rm -f logs/*.log
    
    print_success "Cleanup completed"
}

# Main execution
main() {
    echo "Stopping all MergeEats services..."
    
    stop_backend_services
    stop_frontend
    stop_infrastructure
    cleanup_logs
    
    echo ""
    print_success "🎉 All MergeEats services stopped successfully!"
    echo ""
    echo "📋 To restart services:"
    echo "   ./scripts/setup/setup.sh"
    echo ""
    echo "🐳 To restart only infrastructure:"
    echo "   cd infrastructure/docker-compose && docker-compose up -d"
    echo ""
}

# Execute main function
main "$@"