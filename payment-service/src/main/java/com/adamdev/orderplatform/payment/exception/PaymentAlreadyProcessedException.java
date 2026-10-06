package com.adamdev.orderplatform.payment.exception;

import java.util.UUID;

public class PaymentAlreadyProcessedException extends RuntimeException {

    private final UUID orderId;

    public PaymentAlreadyProcessedException(UUID orderId) {
        super("Payment already exists for order: " + orderId);
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }
}