package com.mergeeats.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service Routes
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .rewritePath("/api/users/(?<segment>.*)", "/api/users/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("http://localhost:8081"))
                
                // Order Service Routes
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f
                                .rewritePath("/api/orders/(?<segment>.*)", "/api/orders/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("http://localhost:8082"))
                
                // Restaurant Service Routes
                .route("restaurant-service", r -> r
                        .path("/api/restaurants/**")
                        .filters(f -> f
                                .rewritePath("/api/restaurants/(?<segment>.*)", "/api/restaurants/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("http://localhost:8083"))
                
                // Payment Service Routes
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f
                                .rewritePath("/api/payments/(?<segment>.*)", "/api/payments/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("http://localhost:8084"))
                
                // Delivery Service Routes
                .route("delivery-service", r -> r
                        .path("/api/deliveries/**")
                        .filters(f -> f
                                .rewritePath("/api/deliveries/(?<segment>.*)", "/api/deliveries/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("http://localhost:8085"))
                
                // Notification Service Routes
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .rewritePath("/api/notifications/(?<segment>.*)", "/api/notifications/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("http://localhost:8086"))
                
                // WebSocket Routes
                .route("websocket-orders", r -> r
                        .path("/ws/orders/**")
                        .uri("ws://localhost:8082"))
                
                .route("websocket-deliveries", r -> r
                        .path("/ws/deliveries/**")
                        .uri("ws://localhost:8085"))
                
                .build();
    }
} 