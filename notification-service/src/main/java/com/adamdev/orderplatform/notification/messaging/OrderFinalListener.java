package com.adamdev.orderplatform.notification.messaging;

import com.adamdev.orderplatform.events.OrderFinalEvent;
import com.adamdev.orderplatform.events.Topics;
import com.adamdev.orderplatform.notification.dto.NotificationRequest;
import com.adamdev.orderplatform.notification.model.NotificationType;
import com.adamdev.orderplatform.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderFinalListener {

    private static final Logger log = LoggerFactory.getLogger(OrderFinalListener.class);

    private final NotificationService notificationService;

    public OrderFinalListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = Topics.ORDER_FINAL_EVENTS)
    public void onOrderFinal(OrderFinalEvent event) {
        log.info("Received order-final for order {} with status {}", event.orderId(), event.status());

        NotificationType type;
        String message;

        if ("CONFIRMED".equals(event.status())) {
            type = NotificationType.ORDER_CONFIRMED;
            message = String.format(
                    "Your order %s has been confirmed. Total: %.2f EUR",
                    event.orderId(),
                    event.totalAmountCents() / 100.0
            );
        } else {
            type = NotificationType.ORDER_CANCELLED;
            message = String.format(
                    "Your order %s has been cancelled. Reason: %s",
                    event.orderId(),
                    event.failureReason()
            );
        }

        NotificationRequest request = new NotificationRequest(
                event.orderId(),
                event.customerId(),
                type,
                message
        );
        notificationService.create(request);
    }
}