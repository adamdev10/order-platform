package com.adamdev.orderplatform.payment.dto;

import com.adamdev.orderplatform.payment.model.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID paymentId,
        UUID orderId,
        PaymentStatus status,
        long amountCents,
        String currency,
        String transactionId,
        String failureReason,
        Instant createdAt
) {
}