package com.mergeeats.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter implements GlobalFilter, Ordered {

    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> lastResetTime = new ConcurrentHashMap<>();
    
    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final long RESET_INTERVAL = 60000; // 1 minute

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String clientIp = getClientIp(request);
        
        if (isRateLimitExceeded(clientIp)) {
            return onError(exchange, "Rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS);
        }
        
        return chain.filter(exchange);
    }

    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddress() != null ? 
               request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    private boolean isRateLimitExceeded(String clientIp) {
        long currentTime = System.currentTimeMillis();
        
        // Reset counter if interval has passed
        if (shouldResetCounter(clientIp, currentTime)) {
            requestCounts.put(clientIp, new AtomicInteger(0));
            lastResetTime.put(clientIp, currentTime);
        }
        
        AtomicInteger counter = requestCounts.computeIfAbsent(clientIp, k -> new AtomicInteger(0));
        return counter.incrementAndGet() > MAX_REQUESTS_PER_MINUTE;
    }

    private boolean shouldResetCounter(String clientIp, long currentTime) {
        Long lastReset = lastResetTime.get(clientIp);
        return lastReset == null || (currentTime - lastReset) > RESET_INTERVAL;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -50; // After authentication filter
    }
} 