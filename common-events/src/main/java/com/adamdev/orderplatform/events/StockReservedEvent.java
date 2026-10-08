package com.adamdev.orderplatform.events;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record StockReservedEvent(
        UUID orderId,
        UUID reservationId,
        String customerId,
        String paymentMethodToken,
        List<StockReservedItem> items,
        long totalAmountCents,
        String currency,
        Instant reservedAt
) {
}