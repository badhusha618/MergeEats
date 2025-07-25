package com.mergeeats.paymentservice.controller;

import com.mergeeats.common.models.Payment;
import com.mergeeats.paymentservice.dto.CreatePaymentRequest;
import com.mergeeats.paymentservice.dto.RefundRequest;
import com.mergeeats.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Management", description = "APIs for managing payments, refunds, and transactions")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Payment Management APIs

    @PostMapping
    @Operation(summary = "Create payment", description = "Create a new payment for an order")
    @ApiResponse(responseCode = "201", description = "Payment created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "409", description = "Payment already exists for order")
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        try {
            Payment payment = paymentService.createPayment(request);
            return new ResponseEntity<>(payment, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment", description = "Get payment details by ID")
    @ApiResponse(responseCode = "200", description = "Payment found")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<Payment> getPayment(
            @Parameter(description = "Payment ID") @PathVariable String paymentId) {
        try {
            Payment payment = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID", description = "Get payment details by order ID")
    @ApiResponse(responseCode = "200", description = "Payment found")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<Payment> getPaymentByOrderId(
            @Parameter(description = "Order ID") @PathVariable String orderId) {
        try {
            Payment payment = paymentService.getPaymentByOrderId(orderId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user payments", description = "Get all payments for a user")
    @ApiResponse(responseCode = "200", description = "Payments retrieved")
    public ResponseEntity<List<Payment>> getUserPayments(
            @Parameter(description = "User ID") @PathVariable String userId) {
        List<Payment> payments = paymentService.getPaymentsByUser(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status", description = "Get all payments with specified status")
    @ApiResponse(responseCode = "200", description = "Payments retrieved")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(
            @Parameter(description = "Payment status") @PathVariable String status) {
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/group/{groupPaymentId}")
    @Operation(summary = "Get group payments", description = "Get all payments for a group payment")
    @ApiResponse(responseCode = "200", description = "Group payments retrieved")
    public ResponseEntity<List<Payment>> getGroupPayments(
            @Parameter(description = "Group payment ID") @PathVariable String groupPaymentId) {
        List<Payment> payments = paymentService.getGroupPayments(groupPaymentId);
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/{paymentId}/retry")
    @Operation(summary = "Retry payment", description = "Retry a failed payment")
    @ApiResponse(responseCode = "200", description = "Payment retry initiated")
    @ApiResponse(responseCode = "400", description = "Cannot retry payment")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<Payment> retryPayment(
            @Parameter(description = "Payment ID") @PathVariable String paymentId) {
        try {
            Payment payment = paymentService.retryPayment(paymentId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{paymentId}/cancel")
    @Operation(summary = "Cancel payment", description = "Cancel a pending payment")
    @ApiResponse(responseCode = "200", description = "Payment cancelled")
    @ApiResponse(responseCode = "400", description = "Cannot cancel payment")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<Payment> cancelPayment(
            @Parameter(description = "Payment ID") @PathVariable String paymentId,
            @Parameter(description = "Cancellation reason") @RequestParam String reason) {
        try {
            Payment payment = paymentService.cancelPayment(paymentId, reason);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Refund Management APIs

    @PostMapping("/refund")
    @Operation(summary = "Process refund", description = "Process a refund for a completed payment")
    @ApiResponse(responseCode = "200", description = "Refund processed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid refund request")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<Payment> processRefund(@Valid @RequestBody RefundRequest request) {
        try {
            Payment refund = paymentService.processRefund(request);
            return ResponseEntity.ok(refund);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Statistics APIs

    @GetMapping("/statistics")
    @Operation(summary = "Get payment statistics", description = "Get overall payment statistics")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved")
    public ResponseEntity<Map<String, Object>> getPaymentStatistics() {
        Map<String, Object> stats = paymentService.getPaymentStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/user/{userId}")
    @Operation(summary = "Get user payment statistics", description = "Get payment statistics for a specific user")
    @ApiResponse(responseCode = "200", description = "User statistics retrieved")
    public ResponseEntity<Map<String, Object>> getUserPaymentStatistics(
            @Parameter(description = "User ID") @PathVariable String userId) {
        Map<String, Object> stats = paymentService.getUserPaymentStatistics(userId);
        return ResponseEntity.ok(stats);
    }

    // Health Check

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check payment service health")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "payment-service",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(health);
    }
}