package com.adamdev.orderplatform.inventory.dto;

import com.adamdev.orderplatform.inventory.model.ReservationStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReservationResponse(
        UUID reservationId,
        UUID orderId,
        ReservationStatus status,
        long totalAmountCents,
        Instant createdAt,
        List<ReservationItemResponse> items
) {
}