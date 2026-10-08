package com.adamdev.orderplatform.events;

import java.time.Instant;
import java.util.UUID;

public record StockReleaseEvent(
        UUID orderId,
        UUID reservationId,
        Instant releasedAt
) {
}