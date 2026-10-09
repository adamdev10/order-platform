package com.adamdev.orderplatform.order.messaging;

import com.adamdev.orderplatform.events.OrderCreatedEvent;
import com.adamdev.orderplatform.events.OrderFinalEvent;
import com.adamdev.orderplatform.events.StockReleaseEvent;
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
        send(Topics.ORDER_CREATED, event.orderId().toString(), event);
    }

    public void publishStockRelease(StockReleaseEvent event) {
        send(Topics.STOCK_RELEASE, event.orderId().toString(), event);
    }

    public void publishOrderFinal(OrderFinalEvent event) {
        send(Topics.ORDER_FINAL_EVENTS, event.orderId().toString(), event);
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