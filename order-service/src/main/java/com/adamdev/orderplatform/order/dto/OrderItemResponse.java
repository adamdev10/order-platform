package com.adamdev.orderplatform.order.dto;

public record OrderItemResponse(
        String productId,
        int quantity
) {
}
