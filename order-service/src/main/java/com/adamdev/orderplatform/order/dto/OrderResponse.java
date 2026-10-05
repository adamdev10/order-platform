package com.adamdev.orderplatform.order.dto;

import com.adamdev.orderplatform.order.model.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        String customerId,
        OrderStatus status,
        Long totalAmountCents,
        Instant createdAt,
        List<OrderItemResponse> items
) {
}
