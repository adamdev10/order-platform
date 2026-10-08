package com.adamdev.orderplatform.payment.messaging;

import com.adamdev.orderplatform.events.PaymentCompletedEvent;
import com.adamdev.orderplatform.events.PaymentFailedEvent;
import com.adamdev.orderplatform.events.StockReservedEvent;
import com.adamdev.orderplatform.events.Topics;
import com.adamdev.orderplatform.payment.dto.PaymentRequest;
import com.adamdev.orderplatform.payment.dto.PaymentResponse;
import com.adamdev.orderplatform.payment.model.PaymentStatus;
import com.adamdev.orderplatform.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class StockReservedListener {

    private static final Logger log = LoggerFactory.getLogger(StockReservedListener.class);

    private final PaymentService paymentService;
    private final PaymentEventPublisher eventPublisher;

    public StockReservedListener(
            PaymentService paymentService,
            PaymentEventPublisher eventPublisher
    ) {
        this.paymentService = paymentService;
        this.eventPublisher = eventPublisher;
    }

    @KafkaListener(topics = Topics.STOCK_RESERVED)
    public void onStockReserved(StockReservedEvent event) {
        log.info("Received stock-reserved for order {}", event.orderId());

        PaymentRequest request = new PaymentRequest(
                event.orderId(),
                event.customerId(),
                event.paymentMethodToken(),
                event.totalAmountCents(),
                event.currency()
        );

        PaymentResponse response = paymentService.charge(request);

        if (response.status() == PaymentStatus.COMPLETED) {
            PaymentCompletedEvent completed = new PaymentCompletedEvent(
                    response.orderId(),
                    response.paymentId(),
                    response.transactionId(),
                    response.amountCents(),
                    response.currency(),
                    Instant.now()
            );
            eventPublisher.publishPaymentCompleted(completed);
        } else {
            PaymentFailedEvent failed = new PaymentFailedEvent(
                    response.orderId(),
                    response.paymentId(),
                    response.failureReason(),
                    response.amountCents(),
                    response.currency(),
                    Instant.now()
            );
            eventPublisher.publishPaymentFailed(failed);
        }
    }
}