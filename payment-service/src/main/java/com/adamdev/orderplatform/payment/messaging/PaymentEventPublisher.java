package com.adamdev.orderplatform.payment.messaging;

import com.adamdev.orderplatform.events.PaymentCompletedEvent;
import com.adamdev.orderplatform.events.PaymentFailedEvent;
import com.adamdev.orderplatform.events.Topics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        send(Topics.PAYMENT_COMPLETED, event.orderId().toString(), event);
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        send(Topics.PAYMENT_FAILED, event.orderId().toString(), event);
    }

    private void send(String topic, String key, Object event) {
        kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish to {} for key {}", topic, key, ex);
            } else {
                log.info("Published to {} for key {} partition {}",
                        topic, key, result.getRecordMetadata().partition());
            }
        });
    }
}