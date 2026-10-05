package com.adamdev.orderplatform.inventory.dto;

public record ReservationItemResponse(
        String productId,
        int quantity,
        long unitPriceCents
) {
}