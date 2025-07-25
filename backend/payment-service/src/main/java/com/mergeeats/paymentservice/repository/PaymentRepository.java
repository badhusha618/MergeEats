package com.mergeeats.paymentservice.repository;

import com.mergeeats.common.models.Payment;
import com.mergeeats.common.enums.PaymentStatus;
import com.mergeeats.common.enums.PaymentMethod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    // Find payments by order ID
    List<Payment> findByOrderId(String orderId);
    
    // Find the latest payment for an order
    Optional<Payment> findTopByOrderIdOrderByCreatedAtDesc(String orderId);

    // Find payments by customer ID
    List<Payment> findByCustomerId(String customerId);

    // Find payments by status
    List<Payment> findByStatus(PaymentStatus status);

    // Find payments by payment method
    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);

    // Find payments by gateway transaction ID
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);

    // Find payments by gateway and external transaction ID
    Optional<Payment> findByPaymentGatewayAndGatewayTransactionId(String paymentGateway, String gatewayTransactionId);

    // Find payments within date range
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find payments by customer and date range
    List<Payment> findByCustomerIdAndCreatedAtBetween(String customerId, LocalDateTime startDate, LocalDateTime endDate);

    // Find failed payments that can be retried
    @Query("{'status': 'FAILED', 'retryCount': {'$lt': ?0}, 'createdAt': {'$gte': ?1}}")
    List<Payment> findRetryableFailedPayments(int maxRetryCount, LocalDateTime minCreatedAt);

    // Find pending payments older than specified time
    @Query("{'status': 'PENDING', 'createdAt': {'$lt': ?0}}")
    List<Payment> findExpiredPendingPayments(LocalDateTime expiredBefore);

    // Find payments by amount range
    List<Payment> findByAmountBetween(Double minAmount, Double maxAmount);

    // Find refunded payments
    List<Payment> findByIsRefunded(boolean isRefunded);

    // Find payments by invoice number
    Optional<Payment> findByInvoiceNumber(String invoiceNumber);

    // Find payments requiring manual review (high retry count or specific conditions)
    @Query("{'$or': [{'retryCount': {'$gte': ?0}}, {'status': 'FAILED', 'amount': {'$gte': ?1}}]}")
    List<Payment> findPaymentsRequiringReview(int highRetryCount, Double highAmountThreshold);

    // Count payments by status
    long countByStatus(PaymentStatus status);

    // Count payments by payment method
    long countByPaymentMethod(PaymentMethod paymentMethod);

    // Find payments by multiple order IDs (for group orders)
    List<Payment> findByOrderIdIn(List<String> orderIds);

    // Find payments by group order ID
    List<Payment> findByGroupOrderId(String groupOrderId);

    // Calculate total amount by customer and date range
    @Query(value = "{'customerId': ?0, 'status': 'COMPLETED', 'createdAt': {'$gte': ?1, '$lte': ?2}}", 
           fields = "{'amount': 1}")
    List<Payment> findCompletedPaymentsByCustomerAndDateRange(String customerId, LocalDateTime startDate, LocalDateTime endDate);

    // Find duplicate payments (same order, amount, and recent timeframe)
    @Query("{'orderId': ?0, 'amount': ?1, 'createdAt': {'$gte': ?2}}")
    List<Payment> findPotentialDuplicatePayments(String orderId, Double amount, LocalDateTime recentTimeThreshold);
}