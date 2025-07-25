package com.mergeeats.paymentservice.controller;

import com.mergeeats.common.models.Payment;
import com.mergeeats.common.enums.PaymentMethod;
import com.mergeeats.common.enums.PaymentStatus;
import com.mergeeats.paymentservice.service.PaymentService;
import com.mergeeats.paymentservice.dto.CreatePaymentRequest;
import com.mergeeats.paymentservice.dto.RefundRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments", description = "Payment processing and management operations")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Payment Processing
    @PostMapping("/initiate")
    @Operation(summary = "Initiate payment", description = "Create and initiate a new payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Payment initiated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payment data"),
        @ApiResponse(responseCode = "409", description = "Payment already exists")
    })
    public ResponseEntity<?> initiatePayment(@Valid @RequestBody CreatePaymentRequest request) {
        try {
            // Convert request to Payment entity
            Payment payment = new Payment();
            payment.setOrderId(request.getOrderId());
            payment.setUserId(request.getUserId());
            payment.setAmount(request.getAmount());
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setCurrency(request.getCurrency());
            payment.setIsSplitPayment(request.getIsSplitPayment());
            payment.setGroupOrderId(request.getGroupOrderId());

            Payment initiatedPayment = paymentService.initiatePayment(payment);
            return new ResponseEntity<>(initiatedPayment, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Payment initiation failed", e.getMessage()));
        }
    }

    @PostMapping("/{paymentId}/process")
    @Operation(summary = "Process payment", description = "Process a pending payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "400", description = "Payment processing failed")
    })
    public ResponseEntity<?> processPayment(
            @Parameter(description = "Payment ID", required = true) @PathVariable String paymentId) {
        try {
            Payment processedPayment = paymentService.processPayment(paymentId);
            return ResponseEntity.ok(processedPayment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Payment processing failed", e.getMessage()));
        }
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Refund payment", description = "Process a refund for a completed payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refund processed successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "400", description = "Refund processing failed")
    })
    public ResponseEntity<?> refundPayment(
            @Parameter(description = "Payment ID", required = true) @PathVariable String paymentId,
            @Valid @RequestBody RefundRequest refundRequest) {
        try {
            Payment refundedPayment = paymentService.refundPayment(
                paymentId, 
                refundRequest.getRefundAmount(), 
                refundRequest.getReason()
            );
            return ResponseEntity.ok(refundedPayment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Refund processing failed", e.getMessage()));
        }
    }

    // Split Payment Management
    @PostMapping("/split/initiate")
    @Operation(summary = "Initiate split payment", description = "Create split payments for a group order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Split payments initiated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid split payment data")
    })
    public ResponseEntity<?> initiateSplitPayment(
            @Parameter(description = "Group order ID", required = true) @RequestParam String groupOrderId,
            @Parameter(description = "Split details (userId -> amount)", required = true) @RequestBody Map<String, Double> splitDetails,
            @Parameter(description = "Payment method", required = true) @RequestParam PaymentMethod paymentMethod,
            @Parameter(description = "Currency", required = false) @RequestParam(defaultValue = "INR") String currency) {
        try {
            List<Payment> splitPayments = paymentService.initiateSplitPayment(
                groupOrderId, splitDetails, paymentMethod, currency);
            return new ResponseEntity<>(splitPayments, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Split payment initiation failed", e.getMessage()));
        }
    }

    @GetMapping("/split/{groupOrderId}/status")
    @Operation(summary = "Check group payment status", description = "Check if all payments in a group order are completed")
    @ApiResponse(responseCode = "200", description = "Group payment status retrieved successfully")
    public ResponseEntity<Map<String, Object>> getGroupPaymentStatus(
            @Parameter(description = "Group order ID", required = true) @PathVariable String groupOrderId) {
        boolean isComplete = paymentService.isGroupPaymentComplete(groupOrderId);
        Map<String, Object> response = Map.of(
            "groupOrderId", groupOrderId,
            "isComplete", isComplete
        );
        return ResponseEntity.ok(response);
    }

    // Payment Retrieval
    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Retrieve payment details by payment ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment found"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<?> getPaymentById(
            @Parameter(description = "Payment ID", required = true) @PathVariable String paymentId) {
        Optional<Payment> payment = paymentService.getPaymentById(paymentId);
        if (payment.isPresent()) {
            return ResponseEntity.ok(payment.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payments by order ID", description = "Retrieve all payments for a specific order")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    public ResponseEntity<List<Payment>> getPaymentsByOrderId(
            @Parameter(description = "Order ID", required = true) @PathVariable String orderId) {
        List<Payment> payments = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get payments by user ID", description = "Retrieve all payments for a specific user")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    public ResponseEntity<List<Payment>> getPaymentsByUserId(
            @Parameter(description = "User ID", required = true) @PathVariable String userId) {
        List<Payment> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/user/{userId}/history")
    @Operation(summary = "Get user payment history", description = "Retrieve recent payment history for a user")
    @ApiResponse(responseCode = "200", description = "Payment history retrieved successfully")
    public ResponseEntity<List<Payment>> getUserPaymentHistory(
            @Parameter(description = "User ID", required = true) @PathVariable String userId,
            @Parameter(description = "Number of payments to retrieve", required = false) @RequestParam(defaultValue = "10") int limit) {
        List<Payment> payments = paymentService.getUserPaymentHistory(userId, limit);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status", description = "Retrieve all payments with a specific status")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(
            @Parameter(description = "Payment status", required = true) @PathVariable PaymentStatus status) {
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    // Analytics and Reporting
    @GetMapping("/statistics")
    @Operation(summary = "Get payment statistics", description = "Retrieve payment statistics for a date range")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getPaymentStatistics(
            @Parameter(description = "Start date", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Map<String, Object> statistics = paymentService.getPaymentStatistics(startDate, endDate);
            return ResponseEntity.ok(statistics);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Statistics generation failed", "message", e.getMessage()));
        }
    }

    @GetMapping("/top-transactions")
    @Operation(summary = "Get top transactions", description = "Retrieve the highest value completed transactions")
    @ApiResponse(responseCode = "200", description = "Top transactions retrieved successfully")
    public ResponseEntity<List<Payment>> getTopTransactions(
            @Parameter(description = "Number of transactions to retrieve", required = false) 
            @RequestParam(defaultValue = "10") int limit) {
        List<Payment> topTransactions = paymentService.getTopTransactions(limit);
        return ResponseEntity.ok(topTransactions);
    }

    // Administrative Operations
    @PostMapping("/retry-failed")
    @Operation(summary = "Retry failed payments", description = "Trigger retry process for eligible failed payments")
    @ApiResponse(responseCode = "200", description = "Retry process initiated successfully")
    public ResponseEntity<String> retryFailedPayments() {
        try {
            paymentService.retryFailedPayments();
            return ResponseEntity.ok("Failed payment retry process initiated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to initiate retry process: " + e.getMessage());
        }
    }

    @PostMapping("/timeout-pending")
    @Operation(summary = "Timeout pending payments", description = "Mark old pending payments as failed")
    @ApiResponse(responseCode = "200", description = "Timeout process completed successfully")
    public ResponseEntity<String> timeoutPendingPayments() {
        try {
            paymentService.timeoutPendingPayments();
            return ResponseEntity.ok("Pending payment timeout process completed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to complete timeout process: " + e.getMessage());
        }
    }

    // Health Check
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the payment service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Payment Service is running");
    }

    // Error Response DTO
    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}