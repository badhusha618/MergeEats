package com.mergeeats.paymentservice.controller;

import com.mergeeats.common.models.Payment;
import com.mergeeats.common.enums.PaymentMethod;
import com.mergeeats.common.enums.PaymentStatus;
import com.mergeeats.paymentservice.service.PaymentService;
import com.mergeeats.paymentservice.dto.CreatePaymentRequest;
import com.mergeeats.paymentservice.dto.RefundRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
@Tag(name = "Payment Management", 
     description = "**Comprehensive Payment Management APIs**\n\n" +
                  "This controller provides all payment-related operations including:\n" +
                  "- Payment processing and creation\n" +
                  "- Payment status tracking\n" +
                  "- Refund processing\n" +
                  "- Payment history and analytics\n" +
                  "- Multi-payment method support")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    @Operation(
        summary = "Create a new payment",
        description = "**Creates a new payment record for an order**\n\n" +
                     "This endpoint handles payment creation with:\n" +
                     "- Multiple payment method support\n" +
                     "- Secure payment processing\n" +
                     "- Payment status tracking\n" +
                     "- Transaction logging",
        tags = {"Payment Processing"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Payment created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Payment.class),
                examples = @ExampleObject(
                    name = "Successful Payment Creation",
                    value = """
                        {
                          "id": "payment_123456789",
                          "orderId": "order_123456789",
                          "userId": "user_987654321",
                          "amount": 29.99,
                          "paymentMethod": "CREDIT_CARD",
                          "paymentType": "ONLINE",
                          "status": "PENDING",
                          "transactionId": "txn_987654321",
                          "createdAt": "2024-01-15T11:00:00Z",
                          "updatedAt": "2024-01-15T11:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid payment data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                        {
                          "message": "Validation failed",
                          "errors": [
                            "Order ID is required",
                            "User ID is required",
                            "Amount must be greater than 0"
                          ]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Payment> createPayment(
        @Parameter(
            description = "Payment creation data",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Payment Creation",
                    value = """
                        {
                          "orderId": "order_123456789",
                          "userId": "user_987654321",
                          "amount": 29.99,
                          "paymentMethod": "CREDIT_CARD",
                          "paymentType": "ONLINE"
                        }
                        """
                )
            )
        )
        @Valid @RequestBody CreatePaymentRequest request) {
        try {
            Payment payment = new Payment();
            payment.setOrderId(request.getOrderId());
            payment.setUserId(request.getUserId());
            payment.setAmount(request.getAmount());
            
            // Convert strings to enums safely
            try {
                payment.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
            } catch (Exception e) {
                payment.setPaymentMethod(PaymentMethod.CREDIT_CARD); // default
            }
            
            Payment createdPayment = paymentService.createPayment(payment);
            return ResponseEntity.ok(createdPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{paymentId}/process")
    @Operation(
        summary = "Process payment",
        description = "**Processes a pending payment and updates its status**\n\n" +
                     "This endpoint handles payment processing with:\n" +
                     "- Payment gateway integration\n" +
                     "- Transaction verification\n" +
                     "- Status updates\n" +
                     "- Error handling",
        tags = {"Payment Processing"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Payment processed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Payment.class),
                examples = @ExampleObject(
                    name = "Successful Payment Processing",
                    value = """
                        {
                          "id": "payment_123456789",
                          "orderId": "order_123456789",
                          "userId": "user_987654321",
                          "amount": 29.99,
                          "paymentMethod": "CREDIT_CARD",
                          "paymentType": "ONLINE",
                          "status": "COMPLETED",
                          "transactionId": "txn_987654321",
                          "processedAt": "2024-01-15T11:05:00Z",
                          "createdAt": "2024-01-15T11:00:00Z",
                          "updatedAt": "2024-01-15T11:05:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Payment processing failed",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Processing Error",
                    value = """
                        {
                          "message": "Payment processing failed",
                          "errors": ["Insufficient funds", "Card declined"]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Payment> processPayment(
        @Parameter(example = "payment_123456789", description = "Unique payment identifier")
        @PathVariable String paymentId) {
        try {
            Payment payment = paymentService.processPayment(paymentId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(
        summary = "Process refund",
        description = "**Processes a refund for a completed payment**\n\n" +
                     "This endpoint handles refund processing with:\n" +
                     "- Refund amount validation\n" +
                     "- Refund reason tracking\n" +
                     "- Status updates\n" +
                     "- Notification triggers",
        tags = {"Refund Processing"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Refund processed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Payment.class),
                examples = @ExampleObject(
                    name = "Successful Refund",
                    value = """
                        {
                          "id": "payment_123456789",
                          "orderId": "order_123456789",
                          "userId": "user_987654321",
                          "amount": 29.99,
                          "refundAmount": 29.99,
                          "refundReason": "Customer requested refund due to wrong order",
                          "status": "REFUNDED",
                          "refundedAt": "2024-01-15T12:00:00Z",
                          "createdAt": "2024-01-15T11:00:00Z",
                          "updatedAt": "2024-01-15T12:00:00Z"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Payment> processRefund(
        @Parameter(example = "payment_123456789", description = "Unique payment identifier")
        @PathVariable String paymentId,
        @Parameter(
            description = "Refund request data",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Refund Request",
                    value = """
                        {
                          "amount": 29.99,
                          "reason": "Customer requested refund due to wrong order"
                        }
                        """
                )
            )
        )
        @Valid @RequestBody RefundRequest request) {
        try {
            Payment refundedPayment = paymentService.processRefund(paymentId, request.getAmount(), request.getReason());
            return ResponseEntity.ok(refundedPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(
        summary = "Get all payments",
        description = "**Retrieves all payment records**\n\n" +
                     "Returns comprehensive payment data including:\n" +
                     "- Payment details and status\n" +
                     "- Transaction information\n" +
                     "- User and order references\n" +
                     "- Timestamps and history",
        tags = {"Payment Information"}
    )
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{paymentId}")
    @Operation(
        summary = "Get payment by ID",
        description = "**Retrieves detailed payment information by ID**\n\n" +
                     "Returns comprehensive payment data including:\n" +
                     "- Payment details and status\n" +
                     "- Transaction information\n" +
                     "- Processing history\n" +
                     "- Refund information if applicable",
        tags = {"Payment Information"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Payment found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Payment.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Payment not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "Payment not found",
                          "errors": ["Payment with ID payment_123456789 does not exist"]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Payment> getPayment(
        @Parameter(example = "payment_123456789", description = "Unique payment identifier")
        @PathVariable String paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);
        if (payment != null) {
            return ResponseEntity.ok(payment);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/order/{orderId}")
    @Operation(
        summary = "Get payments by order ID",
        description = "**Retrieves all payments for a specific order**\n\n" +
                     "Useful for tracking payment history for an order",
        tags = {"Payment Information"}
    )
    public ResponseEntity<List<Payment>> getPaymentsByOrderId(
        @Parameter(example = "order_123456789", description = "Order identifier")
        @PathVariable String orderId) {
        List<Payment> payments = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/user/{userId}")
    @Operation(
        summary = "Get payments by user ID",
        description = "**Retrieves all payments for a specific user**\n\n" +
                     "Useful for user payment history and analytics",
        tags = {"Payment Information"}
    )
    public ResponseEntity<List<Payment>> getPaymentsByUserId(
        @Parameter(example = "user_987654321", description = "User identifier")
        @PathVariable String userId) {
        List<Payment> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }
}
