package com.adamdev.orderplatform.events;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        String customerId,
        String paymentMethodToken,
        List<OrderCreatedItem> items,
        Instant createdAt
) {
}