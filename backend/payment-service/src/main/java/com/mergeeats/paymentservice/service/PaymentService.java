package com.mergeeats.paymentservice.service;

import com.mergeeats.common.models.Payment;
import com.mergeeats.paymentservice.dto.CreatePaymentRequest;
import com.mergeeats.paymentservice.dto.RefundRequest;
import com.mergeeats.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${payment.fees.platform-fee-percentage:2.5}")
    private Double platformFeePercentage;

    @Value("${payment.fees.payment-gateway-fee-percentage:2.9}")
    private Double gatewayFeePercentage;

    @Value("${payment.fees.min-platform-fee:5.0}")
    private Double minPlatformFee;

    @Value("${payment.fees.max-platform-fee:50.0}")
    private Double maxPlatformFee;

    @Value("${payment.processing.timeout-seconds:30}")
    private Integer processingTimeoutSeconds;

    @Value("${payment.processing.retry-attempts:3}")
    private Integer maxRetryAttempts;

    // Create payment
    public Payment createPayment(CreatePaymentRequest request) {
        logger.info("Creating payment for order: {}", request.getOrderId());

        // Check if payment already exists for this order
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(request.getOrderId());
        if (existingPayment.isPresent()) {
            throw new RuntimeException("Payment already exists for order: " + request.getOrderId());
        }

        Payment payment = new Payment(
            request.getOrderId(),
            request.getUserId(),
            request.getAmount(),
            request.getPaymentMethod()
        );

        payment.setCurrency(request.getCurrency());
        payment.setGatewayProvider(request.getGatewayProvider());
        payment.setGatewayPaymentMethodId(request.getGatewayPaymentMethodId());
        payment.setCustomerEmail(request.getCustomerEmail());
        payment.setCustomerPhone(request.getCustomerPhone());
        payment.setBillingAddress(request.getBillingAddress());

        // Group payment details
        if (request.getIsGroupPayment()) {
            payment.setIsGroupPayment(true);
            payment.setGroupPaymentId(request.getGroupPaymentId());
            payment.setUserShareAmount(request.getUserShareAmount());
            payment.setTotalParticipants(request.getTotalParticipants());
        }

        // Calculate fees
        calculateFees(payment);

        // Generate transaction reference
        payment.setTransactionReference(generateTransactionReference());

        Payment savedPayment = paymentRepository.save(payment);

        // Publish payment created event
        publishPaymentEvent("PAYMENT_CREATED", savedPayment);

        // Process payment asynchronously
        processPaymentAsync(savedPayment.getPaymentId());

        logger.info("Payment created successfully: {}", savedPayment.getPaymentId());
        return savedPayment;
    }

    // Process payment asynchronously
    @Async
    public CompletableFuture<Payment> processPaymentAsync(String paymentId) {
        try {
            Payment payment = getPaymentById(paymentId);
            return CompletableFuture.completedFuture(processPayment(payment));
        } catch (Exception e) {
            logger.error("Error processing payment asynchronously: {}", paymentId, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    // Process payment
    private Payment processPayment(Payment payment) {
        logger.info("Processing payment: {}", payment.getPaymentId());

        try {
            // Update status to processing
            payment.setPaymentStatus("PROCESSING");
            payment = paymentRepository.save(payment);

            // Publish processing event
            publishPaymentEvent("PAYMENT_PROCESSING", payment);

            // Simulate payment processing based on method
            boolean processingResult = simulatePaymentProcessing(payment);

            if (processingResult) {
                // Payment successful
                payment.setPaymentStatus("COMPLETED");
                payment.setCompletedAt(LocalDateTime.now());
                payment.setAuthorizationCode(generateAuthorizationCode());
                payment.setGatewayTransactionId(generateGatewayTransactionId());
                payment.setReceiptUrl(generateReceiptUrl(payment.getPaymentId()));

                logger.info("Payment completed successfully: {}", payment.getPaymentId());
            } else {
                // Payment failed
                payment.setPaymentStatus("FAILED");
                payment.setFailedAt(LocalDateTime.now());
                payment.setFailureReason("Payment processing failed");
                payment.setFailureCode("PROCESSING_ERROR");

                logger.warn("Payment failed: {}", payment.getPaymentId());
            }

        } catch (Exception e) {
            // Payment failed due to exception
            payment.setPaymentStatus("FAILED");
            payment.setFailedAt(LocalDateTime.now());
            payment.setFailureReason("Exception during processing: " + e.getMessage());
            payment.setFailureCode("SYSTEM_ERROR");

            logger.error("Payment processing exception: {}", payment.getPaymentId(), e);
        }

        Payment updatedPayment = paymentRepository.save(payment);

        // Publish completion event
        publishPaymentEvent("PAYMENT_COMPLETED", updatedPayment);

        return updatedPayment;
    }

    // Retry failed payment
    public Payment retryPayment(String paymentId) {
        Payment payment = getPaymentById(paymentId);

        if (!"FAILED".equals(payment.getPaymentStatus())) {
            throw new RuntimeException("Can only retry failed payments");
        }

        if (payment.getRetryAttempts() >= payment.getMaxRetryAttempts()) {
            throw new RuntimeException("Maximum retry attempts exceeded");
        }

        // Increment retry count
        payment.setRetryAttempts(payment.getRetryAttempts() + 1);
        payment.setNextRetryAt(LocalDateTime.now().plusMinutes(5)); // 5 minute delay

        // Reset status to pending
        payment.setPaymentStatus("PENDING");
        payment.setFailedAt(null);
        payment.setFailureReason(null);
        payment.setFailureCode(null);

        Payment updatedPayment = paymentRepository.save(payment);

        // Process payment again
        processPaymentAsync(paymentId);

        logger.info("Payment retry initiated: {} (attempt {})", paymentId, payment.getRetryAttempts());
        return updatedPayment;
    }

    // Cancel payment
    public Payment cancelPayment(String paymentId, String reason) {
        Payment payment = getPaymentById(paymentId);

        if ("COMPLETED".equals(payment.getPaymentStatus())) {
            throw new RuntimeException("Cannot cancel completed payment. Use refund instead.");
        }

        payment.setPaymentStatus("CANCELLED");
        payment.setCancelledAt(LocalDateTime.now());
        payment.setFailureReason(reason);

        Payment updatedPayment = paymentRepository.save(payment);

        // Publish cancellation event
        publishPaymentEvent("PAYMENT_CANCELLED", updatedPayment);

        logger.info("Payment cancelled: {} - {}", paymentId, reason);
        return updatedPayment;
    }

    // Process refund
    public Payment processRefund(RefundRequest request) {
        Payment originalPayment = getPaymentById(request.getPaymentId());

        if (!"COMPLETED".equals(originalPayment.getPaymentStatus())) {
            throw new RuntimeException("Can only refund completed payments");
        }

        if (request.getRefundAmount() > originalPayment.getAmount()) {
            throw new RuntimeException("Refund amount cannot exceed original payment amount");
        }

        // Create refund payment record
        Payment refundPayment = new Payment();
        refundPayment.setOrderId(originalPayment.getOrderId());
        refundPayment.setUserId(originalPayment.getUserId());
        refundPayment.setAmount(request.getRefundAmount());
        refundPayment.setCurrency(originalPayment.getCurrency());
        refundPayment.setPaymentMethod(originalPayment.getPaymentMethod());
        refundPayment.setPaymentStatus("PROCESSING");
        refundPayment.setPaymentType(request.getIsPartialRefund() ? "PARTIAL_REFUND" : "REFUND");
        refundPayment.setGatewayProvider(originalPayment.getGatewayProvider());
        refundPayment.setRefundReason(request.getRefundReason());
        refundPayment.setRefundInitiatedAt(LocalDateTime.now());
        refundPayment.setTransactionReference(generateTransactionReference());

        Payment savedRefund = paymentRepository.save(refundPayment);

        // Update original payment
        originalPayment.setRefundAmount(request.getRefundAmount());
        originalPayment.setRefundReason(request.getRefundReason());
        originalPayment.setRefundReference(savedRefund.getTransactionReference());
        originalPayment.setRefundInitiatedAt(LocalDateTime.now());

        if (request.getIsPartialRefund()) {
            originalPayment.setPaymentStatus("PARTIALLY_REFUNDED");
        } else {
            originalPayment.setPaymentStatus("REFUNDED");
        }

        paymentRepository.save(originalPayment);

        // Simulate refund processing
        try {
            Thread.sleep(2000); // Simulate processing time
            
            savedRefund.setPaymentStatus("COMPLETED");
            savedRefund.setCompletedAt(LocalDateTime.now());
            savedRefund.setGatewayTransactionId(generateGatewayTransactionId());
            
            originalPayment.setRefundCompletedAt(LocalDateTime.now());
            
            logger.info("Refund processed successfully: {}", savedRefund.getPaymentId());
        } catch (Exception e) {
            savedRefund.setPaymentStatus("FAILED");
            savedRefund.setFailedAt(LocalDateTime.now());
            savedRefund.setFailureReason("Refund processing failed");
            
            logger.error("Refund processing failed: {}", savedRefund.getPaymentId(), e);
        }

        Payment finalRefund = paymentRepository.save(savedRefund);
        paymentRepository.save(originalPayment);

        // Publish refund event
        publishPaymentEvent("REFUND_PROCESSED", finalRefund);

        return finalRefund;
    }

    // Get payment by ID
    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
    }

    // Get payment by order ID
    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }

    // Get payments by user
    public List<Payment> getPaymentsByUser(String userId) {
        return paymentRepository.findByUserId(userId);
    }

    // Get payments by status
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByPaymentStatus(status);
    }

    // Get group payments
    public List<Payment> getGroupPayments(String groupPaymentId) {
        return paymentRepository.findByGroupPaymentId(groupPaymentId);
    }

    // Get payment statistics
    public Map<String, Object> getPaymentStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalPayments", paymentRepository.count());
        stats.put("completedPayments", paymentRepository.countByPaymentStatus("COMPLETED"));
        stats.put("failedPayments", paymentRepository.countByPaymentStatus("FAILED"));
        stats.put("cancelledPayments", paymentRepository.countByPaymentStatus("CANCELLED"));
        stats.put("refundedPayments", paymentRepository.countByPaymentStatus("REFUNDED"));
        stats.put("partiallyRefundedPayments", paymentRepository.countByPaymentStatus("PARTIALLY_REFUNDED"));

        // Calculate success rate
        long total = paymentRepository.count();
        long completed = paymentRepository.countByPaymentStatus("COMPLETED");
        if (total > 0) {
            double successRate = (completed * 100.0) / total;
            stats.put("successRate", Math.round(successRate * 100.0) / 100.0);
        }

        // Group payment statistics
        stats.put("groupPayments", paymentRepository.countByPaymentMethod("GROUP_PAYMENT"));

        // Payment method breakdown
        Map<String, Long> methodBreakdown = new HashMap<>();
        methodBreakdown.put("CREDIT_CARD", paymentRepository.countByPaymentMethod("CREDIT_CARD"));
        methodBreakdown.put("DEBIT_CARD", paymentRepository.countByPaymentMethod("DEBIT_CARD"));
        methodBreakdown.put("DIGITAL_WALLET", paymentRepository.countByPaymentMethod("DIGITAL_WALLET"));
        methodBreakdown.put("UPI", paymentRepository.countByPaymentMethod("UPI"));
        methodBreakdown.put("CASH", paymentRepository.countByPaymentMethod("CASH"));
        stats.put("paymentMethodBreakdown", methodBreakdown);

        return stats;
    }

    // Get user payment statistics
    public Map<String, Object> getUserPaymentStatistics(String userId) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalPayments", paymentRepository.countByUserId(userId));
        
        List<Payment> userPayments = paymentRepository.findByUserId(userId);
        
        long completed = userPayments.stream().mapToLong(p -> "COMPLETED".equals(p.getPaymentStatus()) ? 1 : 0).sum();
        long failed = userPayments.stream().mapToLong(p -> "FAILED".equals(p.getPaymentStatus()) ? 1 : 0).sum();
        
        stats.put("completedPayments", completed);
        stats.put("failedPayments", failed);

        // Total amount spent
        double totalAmount = userPayments.stream()
            .filter(p -> "COMPLETED".equals(p.getPaymentStatus()))
            .mapToDouble(Payment::getAmount)
            .sum();
        stats.put("totalAmountSpent", Math.round(totalAmount * 100.0) / 100.0);

        return stats;
    }

    // Helper method to calculate fees
    private void calculateFees(Payment payment) {
        Double amount = payment.getAmount();
        
        // Calculate platform fee
        Double platformFee = (amount * platformFeePercentage) / 100.0;
        platformFee = Math.max(minPlatformFee, Math.min(maxPlatformFee, platformFee));
        payment.setPlatformFee(platformFee);

        // Calculate gateway fee
        Double gatewayFee = (amount * gatewayFeePercentage) / 100.0;
        payment.setGatewayFee(gatewayFee);

        // Calculate net amount (amount - fees)
        Double netAmount = amount - platformFee - gatewayFee;
        payment.setNetAmount(Math.max(0.0, netAmount));
    }

    // Helper method to simulate payment processing
    private boolean simulatePaymentProcessing(Payment payment) {
        try {
            // Simulate processing time
            Thread.sleep(1000 + (int)(Math.random() * 2000));

            // Simulate different success rates based on payment method
            double successRate = switch (payment.getPaymentMethod()) {
                case "CREDIT_CARD", "DEBIT_CARD" -> 0.95;
                case "DIGITAL_WALLET" -> 0.98;
                case "UPI" -> 0.92;
                case "CASH" -> 1.0;
                default -> 0.90;
            };

            return Math.random() < successRate;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // Helper method to generate transaction reference
    private String generateTransactionReference() {
        return "TXN" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    // Helper method to generate authorization code
    private String generateAuthorizationCode() {
        return "AUTH" + System.currentTimeMillis();
    }

    // Helper method to generate gateway transaction ID
    private String generateGatewayTransactionId() {
        return "GW" + System.currentTimeMillis() + (int)(Math.random() * 10000);
    }

    // Helper method to generate receipt URL
    private String generateReceiptUrl(String paymentId) {
        return "https://receipts.mergeeats.com/payment/" + paymentId;
    }

    // Helper method to publish payment events
    private void publishPaymentEvent(String eventType, Payment payment) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", eventType);
        event.put("paymentId", payment.getPaymentId());
        event.put("orderId", payment.getOrderId());
        event.put("userId", payment.getUserId());
        event.put("amount", payment.getAmount());
        event.put("paymentStatus", payment.getPaymentStatus());
        event.put("paymentMethod", payment.getPaymentMethod());
        event.put("isGroupPayment", payment.getIsGroupPayment());
        event.put("timestamp", LocalDateTime.now());

        kafkaTemplate.send("payment-events", event);
    }
}