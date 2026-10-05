package com.adamdev.orderplatform.inventory.dto;

public record ProductResponse(
        String productId,
        String name,
        int stock,
        long priceCents
) {
}