package com.mergeeats.paymentservice.service;

import com.mergeeats.common.models.Payment;
import com.mergeeats.common.enums.PaymentMethod;
import com.mergeeats.common.enums.PaymentType;
import com.mergeeats.common.enums.PaymentStatus;
import com.mergeeats.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // Payment Processing
    public Payment initiatePayment(Payment payment) {
        try {
            // Validate payment data
            validatePaymentData(payment);

            // Set initial values
            payment.setStatus(PaymentStatus.PENDING);
            payment.setRetryCount(0);
            
            // Calculate fees
            calculateFees(payment);

            // Save payment
            Payment savedPayment = paymentRepository.save(payment);

            // Publish payment initiated event
            publishPaymentEvent("payment-initiated", savedPayment);

            logger.info("Payment initiated: {} for order: {}", savedPayment.getPaymentId(), savedPayment.getOrderId());
            return savedPayment;

        } catch (Exception e) {
            logger.error("Error initiating payment for order {}: {}", payment.getOrderId(), e.getMessage());
            throw new RuntimeException("Payment initiation failed: " + e.getMessage());
        }
    }

    public Payment processPayment(String paymentId) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
            if (paymentOpt.isEmpty()) {
                throw new RuntimeException("Payment not found");
            }

            Payment payment = paymentOpt.get();
            
            if (payment.getStatus() != PaymentStatus.PENDING) {
                throw new RuntimeException("Payment is not in pending status");
            }

            // Update status to processing
            payment.setStatus(PaymentStatus.PROCESSING);
            payment = paymentRepository.save(payment);

            // Simulate payment gateway processing
            boolean paymentSuccess = processWithGateway(payment);

            if (paymentSuccess) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setProcessedAt(LocalDateTime.now());
                payment.setGatewayTransactionId(generateTransactionId());
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Gateway processing failed");
                payment.setRetryCount(payment.getRetryCount() + 1);
            }

            Payment savedPayment = paymentRepository.save(payment);

            // Publish payment event
            String eventType = paymentSuccess ? "payment-completed" : "payment-failed";
            publishPaymentEvent(eventType, savedPayment);

            logger.info("Payment {} processed with status: {}", paymentId, savedPayment.getStatus());
            return savedPayment;

        } catch (Exception e) {
            logger.error("Error processing payment {}: {}", paymentId, e.getMessage());
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    public Payment refundPayment(String paymentId, Double refundAmount, String reason) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
            if (paymentOpt.isEmpty()) {
                throw new RuntimeException("Payment not found");
            }

            Payment payment = paymentOpt.get();
            
            if (payment.getStatus() != PaymentStatus.COMPLETED) {
                throw new RuntimeException("Cannot refund non-completed payment");
            }

            double remainingAmount = payment.getAmount() - payment.getRefundedAmount();
            if (refundAmount > remainingAmount) {
                throw new RuntimeException("Refund amount exceeds remaining refundable amount");
            }

            // Process refund
            boolean refundSuccess = processRefundWithGateway(payment, refundAmount);
            
            if (refundSuccess) {
                payment.setRefundedAmount(payment.getRefundedAmount() + refundAmount);
                payment.setRefundReason(reason);
                payment.setRefundedAt(LocalDateTime.now());
                payment.setRefundTransactionId(generateTransactionId());

                // Update payment status if fully refunded
                if (payment.getRefundedAmount().equals(payment.getAmount())) {
                    payment.setStatus(PaymentStatus.REFUNDED);
                    payment.setIsRefunded(true);
                } else {
                    payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
                }

                Payment savedPayment = paymentRepository.save(payment);

                // Publish refund event
                publishRefundEvent(savedPayment, refundAmount, reason);

                logger.info("Refund processed: {} for payment: {}", refundAmount, paymentId);
                return savedPayment;
            } else {
                throw new RuntimeException("Refund processing failed at gateway");
            }

        } catch (Exception e) {
            logger.error("Error processing refund for payment {}: {}", paymentId, e.getMessage());
            throw e;
        }
    }

    // Split Payment Management
    public List<Payment> initiateSplitPayment(String groupOrderId, Map<String, Double> splitDetails, 
                                            PaymentMethod paymentMethod, String currency) {
        try {
            List<Payment> splitPayments = new ArrayList<>();

            for (Map.Entry<String, Double> entry : splitDetails.entrySet()) {
                String userId = entry.getKey();
                Double amount = entry.getValue();

                Payment splitPayment = new Payment();
                splitPayment.setOrderId(groupOrderId);
                splitPayment.setUserId(userId);
                splitPayment.setAmount(amount);
                splitPayment.setPaymentMethod(paymentMethod);
                splitPayment.setCurrency(currency);
                splitPayment.setIsSplitPayment(true);
                splitPayment.setGroupOrderId(groupOrderId);

                Payment initiatedPayment = initiatePayment(splitPayment);
                splitPayments.add(initiatedPayment);
            }

            logger.info("Split payment initiated for group order: {} with {} payments", groupOrderId, splitPayments.size());
            return splitPayments;

        } catch (Exception e) {
            logger.error("Error initiating split payment for group order {}: {}", groupOrderId, e.getMessage());
            throw e;
        }
    }

    public boolean isGroupPaymentComplete(String groupOrderId) {
        List<Payment> groupPayments = paymentRepository.findByGroupOrderId(groupOrderId);
        return groupPayments.stream().allMatch(payment -> payment.getStatus() == PaymentStatus.COMPLETED);
    }

    // Payment Retrieval
    public Optional<Payment> getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    public List<Payment> getPaymentsByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getPaymentsByUserId(String userId) {
        return paymentRepository.findByCustomerId(userId);
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public List<Payment> getUserPaymentHistory(String userId, int limit) {
        return paymentRepository.findByCustomerId(userId)
                .stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Retry Management
    public void retryFailedPayments() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Payment> failedPayments = paymentRepository.findRetryableFailedPayments(3, now.minusHours(1));

            for (Payment payment : failedPayments) {
                if (payment.getRetryCount() < 3) {
                    logger.info("Retrying payment: {}", payment.getPaymentId());
                    processPayment(payment.getPaymentId());
                }
            }

        } catch (Exception e) {
            logger.error("Error during retry process: {}", e.getMessage());
        }
    }

    public void timeoutPendingPayments() {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(30); // 30 minutes timeout
            List<Payment> pendingPayments = paymentRepository.findExpiredPendingPayments(cutoffTime);

            for (Payment payment : pendingPayments) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Payment timeout");
                paymentRepository.save(payment);

                publishPaymentEvent("payment-timeout", payment);
                logger.info("Payment {} timed out", payment.getPaymentId());
            }

        } catch (Exception e) {
            logger.error("Error timing out pending payments: {}", e.getMessage());
        }
    }

    // Analytics and Reporting
    public Map<String, Object> getPaymentStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Total payments
            List<Payment> allPayments = paymentRepository.findByCreatedAtBetween(startDate, endDate);
            stats.put("totalPayments", allPayments.size());

            // Status distribution
            Map<PaymentStatus, Long> statusDistribution = allPayments.stream()
                    .collect(Collectors.groupingBy(Payment::getStatus, Collectors.counting()));
            stats.put("statusDistribution", statusDistribution);

            // Payment method distribution
            Map<PaymentMethod, Long> methodDistribution = allPayments.stream()
                    .collect(Collectors.groupingBy(Payment::getPaymentMethod, Collectors.counting()));
            stats.put("methodDistribution", methodDistribution);

            // Revenue calculation
            double totalRevenue = allPayments.stream()
                    .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                    .mapToDouble(Payment::getAmount)
                    .sum();
            stats.put("totalRevenue", totalRevenue);

            // Platform fees
            double totalPlatformFees = allPayments.stream()
                    .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                    .mapToDouble(Payment::getPlatformFee)
                    .sum();
            stats.put("totalPlatformFees", totalPlatformFees);

            // Gateway fees
            double totalGatewayFees = allPayments.stream()
                    .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                    .mapToDouble(Payment::getPlatformFee)
                    .sum();
            stats.put("totalGatewayFees", totalGatewayFees);

            // Average transaction value
            double avgTransactionValue = allPayments.stream()
                    .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                    .mapToDouble(Payment::getAmount)
                    .average()
                    .orElse(0.0);
            stats.put("averageTransactionValue", avgTransactionValue);

            // Success rate
            long completedPayments = statusDistribution.getOrDefault(PaymentStatus.COMPLETED, 0L);
            double successRate = allPayments.isEmpty() ? 0.0 : (double) completedPayments / allPayments.size() * 100;
            stats.put("successRate", successRate);

            return stats;

        } catch (Exception e) {
            logger.error("Error generating payment statistics: {}", e.getMessage());
            throw e;
        }
    }

    public List<Payment> getTopTransactions(int limit) {
        return paymentRepository.findByStatus(PaymentStatus.COMPLETED)
                .stream()
                .sorted((p1, p2) -> Double.compare(p2.getAmount(), p1.getAmount()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Private Helper Methods
    private void validatePaymentData(Payment payment) {
        if (payment.getOrderId() == null || payment.getOrderId().trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID is required");
        }
        if (payment.getUserId() == null || payment.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (payment.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Payment method is required");
        }
    }

    private void calculateFees(Payment payment) {
        double amount = payment.getAmount();
        
        // Calculate platform fee (2.5% with min 5.0 and max 50.0)
        double platformFee = amount * 0.025;
        platformFee = Math.max(5.0, Math.min(50.0, platformFee));
        payment.setPlatformFee(platformFee);

        // Calculate tax (18% GST on platform fee)
        double taxAmount = platformFee * 0.18;
        payment.setTaxAmount(taxAmount);
    }

    private boolean processWithGateway(Payment payment) {
        // Simulate gateway processing
        // In real implementation, this would integrate with Stripe/Razorpay
        try {
            Thread.sleep(1000); // Simulate processing time
            
            // Simulate 90% success rate
            Random random = new Random();
            boolean success = random.nextDouble() < 0.9;
            
            if (success) {
                payment.setGatewayTransactionId("pay_" + generateTransactionId());
            }
            
            return success;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private boolean processRefundWithGateway(Payment payment, Double refundAmount) {
        // Simulate refund processing
        // In real implementation, this would integrate with payment gateway
        try {
            Thread.sleep(500); // Simulate processing time
            
            // Simulate 95% success rate for refunds
            Random random = new Random();
            return random.nextDouble() < 0.95;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    // Event Publishing
    private void publishPaymentEvent(String eventType, Payment payment) {
        try {
            var event = new Object() {
                public final String paymentId = payment.getPaymentId();
                public final String orderId = payment.getOrderId();
                public final String userId = payment.getUserId();
                public final Double amount = payment.getAmount();
                public final PaymentStatus status = payment.getStatus();
                public final PaymentMethod paymentMethod = payment.getPaymentMethod();
                public final LocalDateTime timestamp = LocalDateTime.now();
            };
            kafkaTemplate.send("payment-events", eventType, event);
        } catch (Exception e) {
            logger.error("Error publishing payment event: {}", e.getMessage());
        }
    }

    private void publishRefundEvent(Payment payment, Double refundAmount, String reason) {
        try {
            var event = new Object() {
                public final String paymentId = payment.getPaymentId();
                public final String orderId = payment.getOrderId();
                public final String userId = payment.getUserId();
                public final Double refundedAmount = refundAmount;
                public final String refundReason = reason;
                public final String refundTransactionId = payment.getRefundTransactionId();
                public final LocalDateTime timestamp = LocalDateTime.now();
            };
            kafkaTemplate.send("payment-refund-events", "refund-processed", event);
        } catch (Exception e) {
            logger.error("Error publishing refund event: {}", e.getMessage());
        }
    }
}