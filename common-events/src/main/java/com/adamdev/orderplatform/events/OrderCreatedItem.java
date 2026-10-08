package com.adamdev.orderplatform.events;

public record OrderCreatedItem(
        String productId,
        int quantity
) {
}