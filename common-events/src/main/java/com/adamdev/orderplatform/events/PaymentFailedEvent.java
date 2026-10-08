package com.adamdev.orderplatform.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailedEvent(
        UUID orderId,
        UUID paymentId,
        String reason,
        long amountCents,
        String currency,
        Instant failedAt
) {
}