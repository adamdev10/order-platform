package com.adamdev.orderplatform.events;

import java.time.Instant;
import java.util.UUID;

public record OrderFinalEvent(
        UUID orderId,
        String customerId,
        String status,
        Long totalAmountCents,
        String failureReason,
        Instant finalizedAt
) {
}