package com.adamdev.orderplatform.order.messaging;

import com.adamdev.orderplatform.events.OrderCreatedEvent;
import com.adamdev.orderplatform.events.Topics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        String key = event.orderId().toString();
        kafkaTemplate.send(Topics.ORDER_CREATED, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish order-created for order {}", key, ex);
                    } else {
                        log.info("Published order-created for order {} to partition {}", key, result.getRecordMetadata().partition());
                    }
                });
    }
}