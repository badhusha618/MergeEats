package com.mergeeats.paymentservice.repository;

import com.mergeeats.common.models.Payment;
import com.mergeeats.common.models.Payment.PaymentMethod;
import com.mergeeats.common.models.Payment.PaymentType;
import com.mergeeats.common.enums.PaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    // Basic queries
    List<Payment> findByOrderId(String orderId);
    
    List<Payment> findByUserId(String userId);
    
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
    
    Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);
    
    List<Payment> findByStatus(PaymentStatus status);

    // Payment method queries
    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);
    
    List<Payment> findByPaymentMethodAndStatus(PaymentMethod paymentMethod, PaymentStatus status);

    // Payment type queries
    List<Payment> findByPaymentType(PaymentType paymentType);
    
    List<Payment> findByPaymentTypeAndStatus(PaymentType paymentType, PaymentStatus status);

    // User-specific queries
    List<Payment> findByUserIdAndStatus(String userId, PaymentStatus status);
    
    List<Payment> findByUserIdAndPaymentMethod(String userId, PaymentMethod paymentMethod);
    
    @Query("{ 'userId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<Payment> findByUserIdAndDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate);

    // Order-specific queries
    List<Payment> findByOrderIdAndStatus(String orderId, PaymentStatus status);
    
    @Query("{ 'orderId': ?0, 'status': { $in: ?1 } }")
    List<Payment> findByOrderIdAndStatusIn(String orderId, List<PaymentStatus> statuses);

    // Group payment queries
    List<Payment> findByIsSplitPaymentTrue();
    
    List<Payment> findByGroupOrderId(String groupOrderId);
    
    List<Payment> findByGroupOrderIdAndStatus(String groupOrderId, PaymentStatus status);

    // Amount-based queries
    @Query("{ 'amount': { $gte: ?0, $lte: ?1 } }")
    List<Payment> findByAmountBetween(Double minAmount, Double maxAmount);
    
    @Query("{ 'amount': { $gte: ?0 } }")
    List<Payment> findByAmountGreaterThanEqual(Double minAmount);

    // Time-based queries
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Payment> findByProcessedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Payment> findByCreatedAtAfter(LocalDateTime date);
    
    List<Payment> findByCreatedAtBefore(LocalDateTime date);

    // Failed payment queries
    @Query("{ 'status': 'FAILED', 'retryCount': { $lt: '$maxRetries' } }")
    List<Payment> findFailedPaymentsEligibleForRetry();
    
    @Query("{ 'status': 'FAILED', 'nextRetryAt': { $lte: ?0 } }")
    List<Payment> findFailedPaymentsDueForRetry(LocalDateTime currentTime);

    // Refund queries
    @Query("{ 'refundAmount': { $gt: 0 } }")
    List<Payment> findPaymentsWithRefunds();
    
    @Query("{ 'refundAmount': { $lt: '$amount' }, 'refundAmount': { $gt: 0 } }")
    List<Payment> findPartiallyRefundedPayments();
    
    @Query("{ 'refundAmount': '$amount', 'refundAmount': { $gt: 0 } }")
    List<Payment> findFullyRefundedPayments();

    // Statistics queries
    @Query(value = "{ 'status': ?0 }", count = true)
    long countByStatus(PaymentStatus status);
    
    @Query(value = "{ 'paymentMethod': ?0 }", count = true)
    long countByPaymentMethod(PaymentMethod paymentMethod);
    
    @Query(value = "{ 'userId': ?0, 'status': 'COMPLETED' }", count = true)
    long countCompletedPaymentsByUser(String userId);

    // Revenue queries
    @Query("{ 'status': 'COMPLETED', 'createdAt': { $gte: ?0, $lte: ?1 } }")
    List<Payment> findCompletedPaymentsInDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("{ 'status': 'COMPLETED', 'paymentMethod': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<Payment> findCompletedPaymentsByMethodInDateRange(PaymentMethod paymentMethod, LocalDateTime startDate, LocalDateTime endDate);

    // Gateway-specific queries
    @Query("{ 'gatewayTransactionId': { $exists: true, $ne: null } }")
    List<Payment> findPaymentsWithGatewayTransactionId();
    
    @Query("{ 'gatewayTransactionId': { $exists: false } }")
    List<Payment> findPaymentsWithoutGatewayTransactionId();

    // Pending payment queries
    @Query("{ 'status': 'PENDING', 'createdAt': { $lt: ?0 } }")
    List<Payment> findPendingPaymentsOlderThan(LocalDateTime cutoffTime);
    
    @Query("{ 'status': 'PROCESSING', 'createdAt': { $lt: ?0 } }")
    List<Payment> findProcessingPaymentsOlderThan(LocalDateTime cutoffTime);

    // Fee analysis queries
    @Query("{ 'platformFee': { $gt: 0 }, 'status': 'COMPLETED' }")
    List<Payment> findCompletedPaymentsWithPlatformFee();
    
    @Query("{ 'gatewayFee': { $gt: 0 }, 'status': 'COMPLETED' }")
    List<Payment> findCompletedPaymentsWithGatewayFee();

    // Complex aggregation queries
    @Query("{ 'userId': ?0, 'status': 'COMPLETED', 'createdAt': { $gte: ?1 } }")
    List<Payment> findUserCompletedPaymentsSince(String userId, LocalDateTime since);
    
    @Query("{ 'orderId': { $in: ?0 }, 'status': { $in: ?1 } }")
    List<Payment> findPaymentsByOrderIdsAndStatuses(List<String> orderIds, List<PaymentStatus> statuses);

    // Currency-based queries
    List<Payment> findByCurrency(String currency);
    
    @Query("{ 'currency': ?0, 'status': 'COMPLETED', 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<Payment> findCompletedPaymentsByCurrencyInDateRange(String currency, LocalDateTime startDate, LocalDateTime endDate);

    // Custom business logic queries
    @Query("{ 'isSplitPayment': true, 'groupOrderId': ?0, 'status': { $ne: 'COMPLETED' } }")
    List<Payment> findIncompleteSplitPaymentsByGroupOrder(String groupOrderId);
    
    @Query("{ 'paymentType': 'ORDER_PAYMENT', 'status': 'COMPLETED', 'amount': { $gte: ?0 } }")
    List<Payment> findLargeCompletedOrderPayments(Double minAmount);

    // Audit and monitoring queries
    @Query("{ 'retryCount': { $gte: ?0 } }")
    List<Payment> findPaymentsWithHighRetryCount(int minRetryCount);
    
    @Query("{ 'failureReason': { $exists: true, $ne: null }, 'status': 'FAILED' }")
    List<Payment> findFailedPaymentsWithReason();
}