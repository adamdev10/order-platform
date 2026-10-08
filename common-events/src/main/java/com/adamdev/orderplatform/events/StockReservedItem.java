package com.adamdev.orderplatform.events;

public record StockReservedItem(
        String productId,
        int quantity,
        long unitPriceCents
) {
}