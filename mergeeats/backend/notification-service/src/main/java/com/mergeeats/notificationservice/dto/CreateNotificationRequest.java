package com.mergeeats.notificationservice.dto;

import com.mergeeats.common.enums.NotificationType;
import com.mergeeats.common.enums.NotificationChannel;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Map;

public class CreateNotificationRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotNull(message = "Notification channel is required")
    private NotificationChannel channel;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    private String message;

    // Related entity information
    private String orderId;
    private String deliveryId;
    private String restaurantId;
    private String paymentId;

    // Notification metadata
    private Map<String, Object> metadata;

    // Template information
    private String templateId;
    private Map<String, String> templateVariables;

    // Channel specific data
    private String recipientEmail;
    private String recipientPhone;
    private String pushDeviceToken;

    // Scheduling
    private LocalDateTime scheduledAt;
    private LocalDateTime expiresAt;

    // Priority and interaction
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 5, message = "Priority cannot exceed 5")
    private Integer priority = 3;

    private Boolean requiresReadReceipt = false;
    private Boolean isActionable = false;
    private String actionUrl;
    private String actionText;

    // Constructors
    public CreateNotificationRequest() {}

    public CreateNotificationRequest(String userId, NotificationType type, NotificationChannel channel,
                                   String title, String message) {
        this.userId = userId;
        this.type = type;
        this.channel = channel;
        this.title = title;
        this.message = message;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Map<String, String> getTemplateVariables() {
        return templateVariables;
    }

    public void setTemplateVariables(Map<String, String> templateVariables) {
        this.templateVariables = templateVariables;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getPushDeviceToken() {
        return pushDeviceToken;
    }

    public void setPushDeviceToken(String pushDeviceToken) {
        this.pushDeviceToken = pushDeviceToken;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getRequiresReadReceipt() {
        return requiresReadReceipt;
    }

    public void setRequiresReadReceipt(Boolean requiresReadReceipt) {
        this.requiresReadReceipt = requiresReadReceipt;
    }

    public Boolean getIsActionable() {
        return isActionable;
    }

    public void setIsActionable(Boolean isActionable) {
        this.isActionable = isActionable;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getActionText() {
        return actionText;
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }
}