package com.adamdev.orderplatform.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReservationItemRequest(
        @NotBlank String productId,
        @Min(1) int quantity
) {
}