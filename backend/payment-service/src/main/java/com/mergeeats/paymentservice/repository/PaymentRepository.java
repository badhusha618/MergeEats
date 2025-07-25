package com.mergeeats.paymentservice.repository;

import com.mergeeats.common.models.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    // Find by order ID
    Optional<Payment> findByOrderId(String orderId);

    // Find payments by user
    List<Payment> findByUserId(String userId);

    // Find by payment status
    List<Payment> findByPaymentStatus(String paymentStatus);

    // Find by payment method
    List<Payment> findByPaymentMethod(String paymentMethod);

    // Find by gateway provider
    List<Payment> findByGatewayProvider(String gatewayProvider);

    // Find by gateway transaction ID
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);

    // Find by transaction reference
    Optional<Payment> findByTransactionReference(String transactionReference);

    // Find payments in date range
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find user payments in date range
    List<Payment> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);

    // Find successful payments
    List<Payment> findByPaymentStatus(String status);

    // Find failed payments
    @Query("{'paymentStatus': 'FAILED', 'retryAttempts': {'$lt': '$maxRetryAttempts'}}")
    List<Payment> findFailedPaymentsEligibleForRetry();

    // Find pending payments older than specified time
    @Query("{'paymentStatus': 'PENDING', 'initiatedAt': {'$lt': ?0}}")
    List<Payment> findPendingPaymentsOlderThan(LocalDateTime cutoffTime);

    // Find group payments
    List<Payment> findByIsGroupPayment(Boolean isGroupPayment);

    // Find payments by group payment ID
    List<Payment> findByGroupPaymentId(String groupPaymentId);

    // Find refund payments
    @Query("{'paymentType': {'$in': ['REFUND', 'PARTIAL_REFUND']}}")
    List<Payment> findRefundPayments();

    // Find payments by amount range
    @Query("{'amount': {'$gte': ?0, '$lte': ?1}}")
    List<Payment> findByAmountBetween(Double minAmount, Double maxAmount);

    // Count payments by status
    long countByPaymentStatus(String paymentStatus);

    // Count payments by user
    long countByUserId(String userId);

    // Count payments by method
    long countByPaymentMethod(String paymentMethod);

    // Find payments requiring manual review
    @Query("{'retryAttempts': {'$gte': '$maxRetryAttempts'}, 'paymentStatus': 'FAILED'}")
    List<Payment> findPaymentsRequiringManualReview();

    // Sum total amount by status
    @Query(value = "{'paymentStatus': ?0}", fields = "{'amount': 1}")
    List<Payment> findPaymentAmountsByStatus(String status);

    // Find recent payments for user
    @Query("{'userId': ?0}")
    List<Payment> findRecentPaymentsByUserId(String userId);
}