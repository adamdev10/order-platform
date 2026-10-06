package com.adamdev.orderplatform.notification.dto;

import com.adamdev.orderplatform.notification.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record NotificationRequest(
        @NotNull UUID orderId,
        @NotBlank String customerId,
        @NotNull NotificationType type,
        @NotBlank @Size(max = 500) String message
) {
}