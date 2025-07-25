package com.mergeeats.common.enums;

public enum NotificationChannel {
    EMAIL("Email", true, false),
    SMS("SMS", true, false),
    PUSH("Push Notification", false, true),
    WEBSOCKET("WebSocket", false, true),
    IN_APP("In-App Notification", false, true);

    private final String displayName;
    private final boolean requiresExternalService;
    private final boolean supportsRealTime;

    NotificationChannel(String displayName, boolean requiresExternalService, boolean supportsRealTime) {
        this.displayName = displayName;
        this.requiresExternalService = requiresExternalService;
        this.supportsRealTime = supportsRealTime;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean requiresExternalService() {
        return requiresExternalService;
    }

    public boolean supportsRealTime() {
        return supportsRealTime;
    }

    public boolean isAsynchronous() {
        return this == EMAIL || this == SMS;
    }

    public boolean isInstant() {
        return this == PUSH || this == WEBSOCKET || this == IN_APP;
    }

    public String getConfigKey() {
        switch (this) {
            case EMAIL:
                return "notification.email.enabled";
            case SMS:
                return "notification.sms.enabled";
            case PUSH:
                return "notification.push.enabled";
            case WEBSOCKET:
                return "notification.websocket.enabled";
            case IN_APP:
                return "notification.in-app.enabled";
            default:
                return "notification.default.enabled";
        }
    }

    public int getDefaultRetryCount() {
        switch (this) {
            case EMAIL:
            case SMS:
                return 3;
            case PUSH:
                return 2;
            case WEBSOCKET:
            case IN_APP:
                return 1;
            default:
                return 1;
        }
    }
}