package com.adamdev.orderplatform.payment.gateway;

public interface PaymentGateway {

    PaymentResult charge(String paymentMethodToken, long amountCents, String currency);
}