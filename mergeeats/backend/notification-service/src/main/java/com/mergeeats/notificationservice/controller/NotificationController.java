package com.mergeeats.notificationservice.controller;

import com.mergeeats.common.models.Notification;
import com.mergeeats.notificationservice.service.NotificationService;
import com.mergeeats.notificationservice.dto.CreateNotificationRequest;
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
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
@Tag(name = "Notification Management", 
     description = "**Comprehensive Notification Management APIs**\n\n" +
                  "This controller provides all notification-related operations including:\n" +
                  "- Notification creation and sending\n" +
                  "- Multi-channel notification support\n" +
                  "- Notification status tracking\n" +
                  "- User notification preferences\n" +
                  "- Notification templates and scheduling")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    @Operation(
        summary = "Create a new notification",
        description = "**Creates and sends a new notification**\n\n" +
                     "This endpoint handles notification creation with:\n" +
                     "- Multi-channel delivery (email, SMS, push)\n" +
                     "- Template-based notifications\n" +
                     "- Priority and scheduling\n" +
                     "- User preferences and targeting",
        tags = {"Notification Creation"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notification created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Notification.class),
                examples = @ExampleObject(
                    name = "Successful Notification Creation",
                    value = """
                        {
                          "id": "notification_123456789",
                          "recipientId": "user_987654321",
                          "type": "ORDER_STATUS_UPDATE",
                          "title": "Your order #12345 has been confirmed and is being prepared!",
                          "message": "Your order from Pizza Palace is being prepared and will be ready for delivery soon.",
                          "channel": "PUSH",
                          "status": "SENT",
                          "priority": 3,
                          "isRead": false,
                          "sentAt": "2024-01-15T11:05:00Z",
                          "createdAt": "2024-01-15T11:00:00Z",
                          "metadata": {
                            "orderId": "order_123",
                            "status": "PREPARING"
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid notification data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                        {
                          "message": "Validation failed",
                          "errors": [
                            "Recipient ID is required",
                            "Notification type is required",
                            "Title is required"
                          ]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Notification> createNotification(
        @Parameter(
            description = "Notification creation data",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Notification Creation",
                    value = """
                        {
                          "recipientId": "user_987654321",
                          "type": "ORDER_STATUS_UPDATE",
                          "title": "Your order #12345 has been confirmed and is being prepared!",
                          "message": "Your order from Pizza Palace is being prepared and will be ready for delivery soon.",
                          "channel": "PUSH",
                          "priority": 3,
                          "metadata": {
                            "orderId": "order_123",
                            "status": "PREPARING"
                          }
                        }
                        """
                )
            )
        )
        @Valid @RequestBody CreateNotificationRequest request) {
        try {
            Notification notification = notificationService.createNotification(request);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(
        summary = "Get all notifications",
        description = "**Retrieves all notification records**\n\n" +
                     "Returns comprehensive notification data including:\n" +
                     "- Notification details and status\n" +
                     "- Recipient information\n" +
                     "- Delivery status\n" +
                     "- Timestamps and history",
        tags = {"Notification Information"}
    )
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{notificationId}")
    @Operation(
        summary = "Get notification by ID",
        description = "**Retrieves detailed notification information by ID**\n\n" +
                     "Returns comprehensive notification data including:\n" +
                     "- Notification details and status\n" +
                     "- Recipient information\n" +
                     "- Delivery and read status\n" +
                     "- Metadata and context",
        tags = {"Notification Information"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notification found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Notification.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Notification not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "Notification not found",
                          "errors": ["Notification with ID notification_123456789 does not exist"]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Notification> getNotification(
        @Parameter(example = "notification_123456789", description = "Unique notification identifier")
        @PathVariable String notificationId) {
        Notification notification = notificationService.getNotificationById(notificationId);
        if (notification != null) {
            return ResponseEntity.ok(notification);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(
        summary = "Get notifications by user ID",
        description = "**Retrieves all notifications for a specific user**\n\n" +
                     "Useful for user notification history and inbox",
        tags = {"Notification Information"}
    )
    public ResponseEntity<List<Notification>> getUserNotifications(
        @Parameter(example = "user_987654321", description = "User identifier")
        @PathVariable String userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}/read")
    @Operation(
        summary = "Mark notification as read",
        description = "**Marks a notification as read by the user**\n\n" +
                     "Updates the notification status and tracks read time",
        tags = {"Notification Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notification marked as read",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Notification.class),
                examples = @ExampleObject(
                    name = "Successful Read Update",
                    value = """
                        {
                          "id": "notification_123456789",
                          "recipientId": "user_987654321",
                          "type": "ORDER_STATUS_UPDATE",
                          "title": "Your order #12345 has been confirmed and is being prepared!",
                          "message": "Your order from Pizza Palace is being prepared and will be ready for delivery soon.",
                          "channel": "PUSH",
                          "status": "READ",
                          "priority": 3,
                          "isRead": true,
                          "readAt": "2024-01-15T11:10:00Z",
                          "sentAt": "2024-01-15T11:05:00Z",
                          "createdAt": "2024-01-15T11:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Notification not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "Notification not found",
                          "errors": ["Notification with ID notification_123456789 does not exist"]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Notification> markAsRead(
        @Parameter(example = "notification_123456789", description = "Unique notification identifier")
        @PathVariable String notificationId) {
        try {
            Notification notification = notificationService.markAsRead(notificationId);
            if (notification != null) {
                return ResponseEntity.ok(notification);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/unread")
    @Operation(
        summary = "Get unread notifications by user ID",
        description = "**Retrieves all unread notifications for a specific user**\n\n" +
                     "Useful for notification badges and unread counts",
        tags = {"Notification Information"}
    )
    public ResponseEntity<List<Notification>> getUnreadNotifications(
        @Parameter(example = "user_987654321", description = "User identifier")
        @PathVariable String userId) {
        List<Notification> notifications = notificationService.getUnreadNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/user/{userId}/read-all")
    @Operation(
        summary = "Mark all user notifications as read",
        description = "**Marks all notifications for a user as read**\n\n" +
                     "Bulk operation for clearing notification inbox",
        tags = {"Notification Management"}
    )
    public ResponseEntity<String> markAllAsRead(
        @Parameter(example = "user_987654321", description = "User identifier")
        @PathVariable String userId) {
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok("All notifications marked as read");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
