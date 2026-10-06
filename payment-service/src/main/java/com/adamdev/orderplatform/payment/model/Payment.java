package com.adamdev.orderplatform.payment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private long amountCents;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column
    private String transactionId;

    @Column
    private String failureReason;

    @Column(nullable = false)
    private Instant createdAt;

    public static Payment completed(
            UUID orderId,
            String customerId,
            long amountCents,
            String currency,
            String transactionId
    ) {
        Payment p = new Payment();
        p.orderId = orderId;
        p.customerId = customerId;
        p.amountCents = amountCents;
        p.currency = currency;
        p.status = PaymentStatus.COMPLETED;
        p.transactionId = transactionId;
        p.createdAt = Instant.now();
        return p;
    }

    public static Payment failed(
            UUID orderId,
            String customerId,
            long amountCents,
            String currency,
            String failureReason
    ) {
        Payment p = new Payment();
        p.orderId = orderId;
        p.customerId = customerId;
        p.amountCents = amountCents;
        p.currency = currency;
        p.status = PaymentStatus.FAILED;
        p.failureReason = failureReason;
        p.createdAt = Instant.now();
        return p;
    }
}