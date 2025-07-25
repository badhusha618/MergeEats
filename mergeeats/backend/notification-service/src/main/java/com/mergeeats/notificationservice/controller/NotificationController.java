package com.mergeeats.notificationservice.controller;

import com.mergeeats.common.models.Notification;
import com.mergeeats.common.enums.NotificationType;
import com.mergeeats.common.enums.NotificationStatus;
import com.mergeeats.notificationservice.service.NotificationService;
import com.mergeeats.notificationservice.dto.CreateNotificationRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notification Management", description = "APIs for managing notifications across different channels")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Create a new notification", description = "Creates a new notification for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Notification created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        try {
            Notification notification = notificationService.createNotification(request);
            return new ResponseEntity<>(notification, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get notification by ID", description = "Retrieves a notification by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification found"),
        @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    @GetMapping("/{notificationId}")
    public ResponseEntity<Notification> getNotificationById(
            @Parameter(description = "Notification ID") @PathVariable String notificationId) {
        Optional<Notification> notification = notificationService.getNotificationById(notificationId);
        return notification.map(n -> ResponseEntity.ok(n))
                          .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Send notification", description = "Manually sends a pending notification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification sent successfully"),
        @ApiResponse(responseCode = "404", description = "Notification not found"),
        @ApiResponse(responseCode = "400", description = "Notification cannot be sent")
    })
    @PostMapping("/{notificationId}/send")
    public ResponseEntity<Notification> sendNotification(
            @Parameter(description = "Notification ID") @PathVariable String notificationId) {
        try {
            Notification notification = notificationService.sendNotification(notificationId);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Mark notification as read", description = "Marks a notification as read by the user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification marked as read"),
        @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markAsRead(
            @Parameter(description = "Notification ID") @PathVariable String notificationId) {
        try {
            Notification notification = notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Cancel notification", description = "Cancels a pending notification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Notification not found"),
        @ApiResponse(responseCode = "400", description = "Notification cannot be cancelled")
    })
    @PutMapping("/{notificationId}/cancel")
    public ResponseEntity<Notification> cancelNotification(
            @Parameter(description = "Notification ID") @PathVariable String notificationId) {
        try {
            Notification notification = notificationService.cancelNotification(notificationId);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get notifications for user", description = "Retrieves all notifications for a specific user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsForUser(
            @Parameter(description = "User ID") @PathVariable String userId) {
        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Get unread notifications for user", description = "Retrieves unread notifications for a specific user")
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotificationsForUser(
            @Parameter(description = "User ID") @PathVariable String userId) {
        List<Notification> notifications = notificationService.getUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Get notifications by order", description = "Retrieves all notifications related to an order")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Notification>> getNotificationsByOrderId(
            @Parameter(description = "Order ID") @PathVariable String orderId) {
        List<Notification> notifications = notificationService.getNotificationsByOrderId(orderId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Get notifications by status", description = "Retrieves notifications by their status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Notification>> getNotificationsByStatus(
            @Parameter(description = "Notification status") @PathVariable NotificationStatus status) {
        List<Notification> notifications = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Get notifications by type", description = "Retrieves notifications by their type")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>> getNotificationsByType(
            @Parameter(description = "Notification type") @PathVariable NotificationType type) {
        List<Notification> notifications = notificationService.getNotificationsByType(type);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Get notification statistics", description = "Retrieves notification statistics and metrics")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getNotificationStatistics() {
        try {
            Map<String, Object> statistics = notificationService.getNotificationStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}