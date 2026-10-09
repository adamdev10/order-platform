package com.adamdev.orderplatform.order.messaging;

import com.adamdev.orderplatform.events.OrderFinalEvent;
import com.adamdev.orderplatform.events.PaymentCompletedEvent;
import com.adamdev.orderplatform.events.PaymentFailedEvent;
import com.adamdev.orderplatform.events.StockReleaseEvent;
import com.adamdev.orderplatform.events.Topics;
import com.adamdev.orderplatform.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PaymentResultListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultListener.class);

    private final OrderService orderService;
    private final OrderEventPublisher eventPublisher;

    public PaymentResultListener(
            OrderService orderService,
            OrderEventPublisher eventPublisher
    ) {
        this.orderService = orderService;
        this.eventPublisher = eventPublisher;
    }

    @KafkaListener(topics = Topics.PAYMENT_COMPLETED)
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received payment-completed for order {}", event.orderId());
        orderService.markConfirmed(event.orderId(), event.amountCents());

        OrderFinalEvent finalEvent = new OrderFinalEvent(
                event.orderId(),
                loadCustomerId(event.orderId()),
                "CONFIRMED",
                event.amountCents(),
                null,
                Instant.now()
        );
        eventPublisher.publishOrderFinal(finalEvent);
    }

    @KafkaListener(topics = Topics.PAYMENT_FAILED)
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.info("Received payment-failed for order {}: {}", event.orderId(), event.reason());
        orderService.markCancelled(event.orderId());

        StockReleaseEvent releaseEvent = new StockReleaseEvent(
                event.orderId(),
                null,
                Instant.now()
        );
        eventPublisher.publishStockRelease(releaseEvent);

        OrderFinalEvent finalEvent = new OrderFinalEvent(
                event.orderId(),
                loadCustomerId(event.orderId()),
                "CANCELLED",
                null,
                event.reason(),
                Instant.now()
        );
        eventPublisher.publishOrderFinal(finalEvent);
    }

    private String loadCustomerId(java.util.UUID orderId) {
        return orderService.loadById(orderId).getCustomerId();
    }
}