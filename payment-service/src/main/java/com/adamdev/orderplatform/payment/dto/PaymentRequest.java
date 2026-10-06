package com.adamdev.orderplatform.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record PaymentRequest(
        @NotNull UUID orderId,
        @NotBlank String customerId,
        @NotBlank String paymentMethodToken,
        @Positive long amountCents,
        @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency
) {
}