package com.adamdev.orderplatform.payment.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class FakePaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(FakePaymentGateway.class);

    private static final long MAX_AMOUNT_CENTS = 50_000L;
    private static final int RANDOM_FAILURE_PERCENT = 10;

    @Override
    public PaymentResult charge(String paymentMethodToken, long amountCents, String currency) {
        simulateLatency();

        if (paymentMethodToken.startsWith("pm_declined")) {
            log.warn("Payment declined for token {}", mask(paymentMethodToken));
            return PaymentResult.failure("CARD_DECLINED");
        }

        if (paymentMethodToken.startsWith("pm_insufficient")) {
            log.warn("Insufficient funds for token {}", mask(paymentMethodToken));
            return PaymentResult.failure("INSUFFICIENT_FUNDS");
        }

        if (amountCents > MAX_AMOUNT_CENTS) {
            log.warn("Amount {} exceeds limit {}", amountCents, MAX_AMOUNT_CENTS);
            return PaymentResult.failure("LIMIT_EXCEEDED");
        }

        if (ThreadLocalRandom.current().nextInt(100) < RANDOM_FAILURE_PERCENT) {
            log.warn("Simulated gateway timeout");
            return PaymentResult.failure("GATEWAY_TIMEOUT");
        }

        String transactionId = "txn-" + UUID.randomUUID();
        log.info("Payment charged: txn={} amount={} {} token={}",
                transactionId, amountCents, currency, mask(paymentMethodToken));
        return PaymentResult.success(transactionId);
    }

    private void simulateLatency() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(200, 800));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String mask(String token) {
        if (token == null || token.length() <= 8) {
            return "****";
        }
        return token.substring(0, 8) + "****";
    }
}