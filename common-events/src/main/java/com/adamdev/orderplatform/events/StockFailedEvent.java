package com.adamdev.orderplatform.events;

import java.time.Instant;
import java.util.UUID;

public record StockFailedEvent(
        UUID orderId,
        String reason,
        String productId,
        int requested,
        int available,
        Instant failedAt
) {
}