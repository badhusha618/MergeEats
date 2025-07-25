package com.mergeeats.notificationservice.repository;

import com.mergeeats.common.models.Notification;
import com.mergeeats.common.enums.NotificationType;
import com.mergeeats.common.enums.NotificationStatus;
import com.mergeeats.common.enums.NotificationChannel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    // Find by user
    List<Notification> findByUserId(String userId);
    List<Notification> findByUserIdAndStatus(String userId, NotificationStatus status);
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    // Find by type
    List<Notification> findByType(NotificationType type);
    List<Notification> findByTypeIn(List<NotificationType> types);

    // Find by channel
    List<Notification> findByChannel(NotificationChannel channel);
    List<Notification> findByChannelIn(List<NotificationChannel> channels);

    // Find by status
    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findByStatusIn(List<NotificationStatus> statuses);

    // Find by related entities
    List<Notification> findByOrderId(String orderId);
    List<Notification> findByDeliveryId(String deliveryId);
    List<Notification> findByRestaurantId(String restaurantId);
    List<Notification> findByPaymentId(String paymentId);

    // Find pending notifications
    @Query("{'status': {'$in': ['PENDING', 'SCHEDULED']}, 'scheduledAt': {'$lte': ?0}}")
    List<Notification> findPendingNotificationsToSend(LocalDateTime currentTime);

    // Find failed notifications for retry
    @Query("{'status': 'FAILED', 'retryCount': {'$lt': ?0}, 'nextRetryAt': {'$lte': ?1}}")
    List<Notification> findFailedNotificationsForRetry(Integer maxRetries, LocalDateTime currentTime);

    // Find expired notifications
    @Query("{'expiresAt': {'$lt': ?0}, 'status': {'$nin': ['DELIVERED', 'READ', 'CANCELLED']}}")
    List<Notification> findExpiredNotifications(LocalDateTime currentTime);

    // Find unread notifications
    @Query("{'userId': ?0, 'status': {'$in': ['DELIVERED', 'SENT']}, 'readAt': null}")
    List<Notification> findUnreadNotifications(String userId);

    // Find high priority notifications
    @Query("{'priority': {'$lte': 2}, 'status': 'PENDING'}")
    List<Notification> findHighPriorityPendingNotifications();

    // Find notifications by time range
    List<Notification> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Notification> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);

    // Count queries
    long countByUserIdAndStatus(String userId, NotificationStatus status);
    long countByStatusAndCreatedAtBetween(NotificationStatus status, LocalDateTime start, LocalDateTime end);
    long countByTypeAndCreatedAtBetween(NotificationType type, LocalDateTime start, LocalDateTime end);

    // Find notifications requiring read receipt
    @Query("{'requiresReadReceipt': true, 'status': 'DELIVERED', 'readAt': null}")
    List<Notification> findNotificationsRequiringReadReceipt();

    // Find actionable notifications
    List<Notification> findByIsActionableAndStatus(Boolean isActionable, NotificationStatus status);

    // Find notifications by template
    List<Notification> findByTemplateId(String templateId);

    // Delete old notifications
    @Query(value = "{'createdAt': {'$lt': ?0}}", delete = true)
    void deleteNotificationsOlderThan(LocalDateTime cutoffDate);
}