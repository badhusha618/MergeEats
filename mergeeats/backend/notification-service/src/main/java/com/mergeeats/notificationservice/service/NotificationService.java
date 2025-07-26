package com.mergeeats.notificationservice.service;

import com.mergeeats.common.models.Notification;
import com.mergeeats.notificationservice.repository.NotificationRepository;
import com.mergeeats.notificationservice.dto.CreateNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public Notification createNotification(CreateNotificationRequest request) {
        try {
            Notification notification = new Notification();
            notification.setUserId(request.getUserId());
            notification.setTitle(request.getTitle());
            notification.setMessage(request.getMessage());
            notification.setCreatedAt(LocalDateTime.now());
            
            Notification savedNotification = notificationRepository.save(notification);
            logger.info("Notification created: {}", savedNotification.getNotificationId());
            return savedNotification;
        } catch (Exception e) {
            logger.error("Error creating notification", e);
            throw new RuntimeException("Failed to create notification", e);
        }
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification getNotificationById(String notificationId) {
        return notificationRepository.findById(notificationId).orElse(null);
    }

    public List<Notification> getNotificationsByUserId(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    public Notification markAsRead(String notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId).orElse(null);
            if (notification != null) {
                notification.setIsRead(true);
                notification.setReadAt(LocalDateTime.now());
                return notificationRepository.save(notification);
            }
            return null;
        } catch (Exception e) {
            logger.error("Error marking notification as read: {}", notificationId, e);
            throw new RuntimeException("Failed to mark notification as read", e);
        }
    }
}
