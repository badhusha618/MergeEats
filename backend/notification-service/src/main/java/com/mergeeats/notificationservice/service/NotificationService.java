package com.mergeeats.notificationservice.service;

import com.mergeeats.common.models.Notification;
import com.mergeeats.common.enums.NotificationType;
import com.mergeeats.common.enums.NotificationStatus;
import com.mergeeats.common.enums.NotificationChannel;
import com.mergeeats.notificationservice.repository.NotificationRepository;
import com.mergeeats.notificationservice.dto.CreateNotificationRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${notification.email.enabled:true}")
    private Boolean emailEnabled;

    @Value("${notification.sms.enabled:false}")
    private Boolean smsEnabled;

    @Value("${notification.push.enabled:true}")
    private Boolean pushEnabled;

    @Value("${notification.websocket.enabled:true}")
    private Boolean websocketEnabled;

    // Create notification
    public Notification createNotification(CreateNotificationRequest request) {
        Notification notification = new Notification(
            request.getUserId(),
            request.getType(),
            request.getChannel(),
            request.getTitle(),
            request.getMessage()
        );

        // Set related entity information
        notification.setOrderId(request.getOrderId());
        notification.setDeliveryId(request.getDeliveryId());
        notification.setRestaurantId(request.getRestaurantId());
        notification.setPaymentId(request.getPaymentId());

        // Set metadata and template info
        notification.setMetadata(request.getMetadata());
        notification.setTemplateId(request.getTemplateId());
        notification.setTemplateVariables(request.getTemplateVariables());

        // Set channel-specific data
        notification.setRecipientEmail(request.getRecipientEmail());
        notification.setRecipientPhone(request.getRecipientPhone());
        notification.setPushDeviceToken(request.getPushDeviceToken());

        // Set scheduling and priority
        notification.setScheduledAt(request.getScheduledAt());
        notification.setExpiresAt(request.getExpiresAt());
        notification.setPriority(request.getPriority() != null ? request.getPriority() : request.getType().getDefaultPriority());

        // Set interaction properties
        notification.setRequiresReadReceipt(request.getRequiresReadReceipt());
        notification.setIsActionable(request.getIsActionable());
        notification.setActionUrl(request.getActionUrl());
        notification.setActionText(request.getActionText());

        // Set max retries based on channel
        notification.setMaxRetries(request.getChannel().getDefaultRetryCount());

        notification = notificationRepository.save(notification);

        // Send immediately if not scheduled and channel supports immediate delivery
        if (notification.getScheduledAt() == null && shouldSendImmediately(notification)) {
            sendNotificationAsync(notification.getNotificationId());
        }

        // Publish creation event
        publishNotificationEvent("notification.created", notification);

        return notification;
    }

    // Send notification asynchronously
    @Async
    public CompletableFuture<Void> sendNotificationAsync(String notificationId) {
        try {
            sendNotification(notificationId);
        } catch (Exception e) {
            System.err.println("Failed to send notification " + notificationId + ": " + e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    // Send notification
    public Notification sendNotification(String notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            throw new RuntimeException("Notification not found: " + notificationId);
        }

        Notification notification = notificationOpt.get();

        // Check if notification is expired
        if (notification.getExpiresAt() != null && notification.getExpiresAt().isBefore(LocalDateTime.now())) {
            notification.setStatus(NotificationStatus.EXPIRED);
            notification.setFailedAt(LocalDateTime.now());
            notification.setFailureReason("Notification expired");
            return notificationRepository.save(notification);
        }

        // Check if channel is enabled
        if (!isChannelEnabled(notification.getChannel())) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setFailedAt(LocalDateTime.now());
            notification.setFailureReason("Channel disabled: " + notification.getChannel().name());
            return notificationRepository.save(notification);
        }

        try {
            // Update status to sending
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);

            // Send based on channel
            boolean success = sendViaChannel(notification);

            if (success) {
                notification.setStatus(NotificationStatus.DELIVERED);
                notification.setDeliveredAt(LocalDateTime.now());
            } else {
                handleSendFailure(notification);
            }

        } catch (Exception e) {
            handleSendFailure(notification);
            notification.setFailureReason(e.getMessage());
        }

        notification = notificationRepository.save(notification);

        // Publish send event
        publishNotificationEvent("notification.sent", notification);

        return notification;
    }

    // Mark notification as read
    public Notification markAsRead(String notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            throw new RuntimeException("Notification not found: " + notificationId);
        }

        Notification notification = notificationOpt.get();
        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());

        notification = notificationRepository.save(notification);

        // Publish read event
        publishNotificationEvent("notification.read", notification);

        return notification;
    }

    // Cancel notification
    public Notification cancelNotification(String notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            throw new RuntimeException("Notification not found: " + notificationId);
        }

        Notification notification = notificationOpt.get();
        
        if (notification.getStatus().isTerminal()) {
            throw new RuntimeException("Cannot cancel notification in terminal status: " + notification.getStatus());
        }

        notification.setStatus(NotificationStatus.CANCELLED);
        notification = notificationRepository.save(notification);

        // Publish cancellation event
        publishNotificationEvent("notification.cancelled", notification);

        return notification;
    }

    // Get notification by ID
    public Optional<Notification> getNotificationById(String notificationId) {
        return notificationRepository.findById(notificationId);
    }

    // Get notifications for user
    public List<Notification> getNotificationsForUser(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Get unread notifications for user
    public List<Notification> getUnreadNotificationsForUser(String userId) {
        return notificationRepository.findUnreadNotifications(userId);
    }

    // Get notifications by order
    public List<Notification> getNotificationsByOrderId(String orderId) {
        return notificationRepository.findByOrderId(orderId);
    }

    // Get notifications by status
    public List<Notification> getNotificationsByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status);
    }

    // Get notifications by type
    public List<Notification> getNotificationsByType(NotificationType type) {
        return notificationRepository.findByType(type);
    }

    // Scheduled task to process pending notifications
    @Scheduled(fixedDelay = 30000) // Run every 30 seconds
    public void processPendingNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> pendingNotifications = notificationRepository.findPendingNotificationsToSend(now);

        for (Notification notification : pendingNotifications) {
            sendNotificationAsync(notification.getNotificationId());
        }
    }

    // Scheduled task to retry failed notifications
    @Scheduled(fixedDelay = 60000) // Run every minute
    public void retryFailedNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> failedNotifications = notificationRepository.findFailedNotificationsForRetry(3, now);

        for (Notification notification : failedNotifications) {
            notification.setRetryCount(notification.getRetryCount() + 1);
            notification.setStatus(NotificationStatus.PENDING);
            notificationRepository.save(notification);
            
            sendNotificationAsync(notification.getNotificationId());
        }
    }

    // Scheduled task to expire old notifications
    @Scheduled(fixedDelay = 300000) // Run every 5 minutes
    public void expireOldNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> expiredNotifications = notificationRepository.findExpiredNotifications(now);

        for (Notification notification : expiredNotifications) {
            notification.setStatus(NotificationStatus.EXPIRED);
            notification.setFailedAt(now);
            notification.setFailureReason("Notification expired");
            notificationRepository.save(notification);
            
            publishNotificationEvent("notification.expired", notification);
        }
    }

    // Get notification statistics
    public Map<String, Object> getNotificationStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Total notifications
        long totalNotifications = notificationRepository.count();
        stats.put("totalNotifications", totalNotifications);

        // Status distribution
        Map<String, Long> statusDistribution = new HashMap<>();
        for (NotificationStatus status : NotificationStatus.values()) {
            long count = notificationRepository.findByStatus(status).size();
            statusDistribution.put(status.name(), count);
        }
        stats.put("statusDistribution", statusDistribution);

        // Channel distribution
        Map<String, Long> channelDistribution = new HashMap<>();
        for (NotificationChannel channel : NotificationChannel.values()) {
            long count = notificationRepository.findByChannel(channel).size();
            channelDistribution.put(channel.name(), count);
        }
        stats.put("channelDistribution", channelDistribution);

        // Type distribution
        Map<String, Long> typeDistribution = new HashMap<>();
        for (NotificationType type : NotificationType.values()) {
            long count = notificationRepository.findByType(type).size();
            typeDistribution.put(type.name(), count);
        }
        stats.put("typeDistribution", typeDistribution);

        // Recent activity (last 24 hours)
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        long recentNotifications = notificationRepository.findByCreatedAtBetween(dayAgo, LocalDateTime.now()).size();
        stats.put("recentNotifications", recentNotifications);

        return stats;
    }

    // Helper method to check if notification should be sent immediately
    private boolean shouldSendImmediately(Notification notification) {
        return notification.getType().requiresImmediateDelivery() || 
               notification.getChannel().isInstant();
    }

    // Helper method to check if channel is enabled
    private boolean isChannelEnabled(NotificationChannel channel) {
        switch (channel) {
            case EMAIL:
                return emailEnabled;
            case SMS:
                return smsEnabled;
            case PUSH:
                return pushEnabled;
            case WEBSOCKET:
            case IN_APP:
                return websocketEnabled;
            default:
                return true;
        }
    }

    // Helper method to send via specific channel
    private boolean sendViaChannel(Notification notification) {
        switch (notification.getChannel()) {
            case EMAIL:
                return sendEmailNotification(notification);
            case SMS:
                return sendSmsNotification(notification);
            case PUSH:
                return sendPushNotification(notification);
            case WEBSOCKET:
                return sendWebSocketNotification(notification);
            case IN_APP:
                return sendInAppNotification(notification);
            default:
                return false;
        }
    }

    // Mock email sending
    private boolean sendEmailNotification(Notification notification) {
        // In a real implementation, this would integrate with email service (SendGrid, SES, etc.)
        try {
            Thread.sleep(100); // Simulate processing time
            return Math.random() > 0.05; // 95% success rate
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // Mock SMS sending
    private boolean sendSmsNotification(Notification notification) {
        // In a real implementation, this would integrate with SMS service (Twilio, etc.)
        try {
            Thread.sleep(150); // Simulate processing time
            return Math.random() > 0.1; // 90% success rate
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // Mock push notification sending
    private boolean sendPushNotification(Notification notification) {
        // In a real implementation, this would integrate with FCM, APNS, etc.
        try {
            Thread.sleep(50); // Simulate processing time
            return Math.random() > 0.03; // 97% success rate
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // WebSocket notification sending
    private boolean sendWebSocketNotification(Notification notification) {
        try {
            // Publish to WebSocket topic
            Map<String, Object> wsMessage = new HashMap<>();
            wsMessage.put("type", "notification");
            wsMessage.put("notificationId", notification.getNotificationId());
            wsMessage.put("userId", notification.getUserId());
            wsMessage.put("title", notification.getTitle());
            wsMessage.put("message", notification.getMessage());
            wsMessage.put("timestamp", LocalDateTime.now());

            kafkaTemplate.send("websocket-notifications", wsMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // In-app notification sending
    private boolean sendInAppNotification(Notification notification) {
        // Similar to WebSocket but stored for later retrieval
        return true; // Always succeeds as it's stored in database
    }

    // Handle send failure
    private void handleSendFailure(Notification notification) {
        notification.setStatus(NotificationStatus.FAILED);
        notification.setFailedAt(LocalDateTime.now());
        notification.setRetryCount(notification.getRetryCount() + 1);

        // Schedule retry if not exceeded max attempts
        if (notification.getRetryCount() < notification.getMaxRetries()) {
            // Exponential backoff: 2^retryCount minutes
            long delayMinutes = (long) Math.pow(2, notification.getRetryCount());
            notification.setNextRetryAt(LocalDateTime.now().plusMinutes(delayMinutes));
        }
    }

    // Publish notification event
    private void publishNotificationEvent(String eventType, Notification notification) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventType);
            event.put("notificationId", notification.getNotificationId());
            event.put("userId", notification.getUserId());
            event.put("type", notification.getType().name());
            event.put("channel", notification.getChannel().name());
            event.put("status", notification.getStatus().name());
            event.put("orderId", notification.getOrderId());
            event.put("timestamp", LocalDateTime.now());

            kafkaTemplate.send("notification-events", event);
        } catch (Exception e) {
            System.err.println("Failed to publish notification event: " + e.getMessage());
        }
    }
}