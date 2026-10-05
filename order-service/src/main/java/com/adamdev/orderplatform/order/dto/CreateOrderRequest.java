package com.adamdev.orderplatform.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank String customerId,
        @NotBlank String paymentMethodToken,
        @NotEmpty @Valid List<OrderItemRequest> items
)
{ }
