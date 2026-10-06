package com.adamdev.orderplatform.notification.dto;

import com.adamdev.orderplatform.notification.model.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID notificationId,
        UUID orderId,
        String customerId,
        NotificationType type,
        String message,
        Instant sentAt
) {
}