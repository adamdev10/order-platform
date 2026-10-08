package com.adamdev.orderplatform.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentCompletedEvent(
        UUID orderId,
        UUID paymentId,
        String transactionId,
        long amountCents,
        String currency,
        Instant completedAt
) {
}