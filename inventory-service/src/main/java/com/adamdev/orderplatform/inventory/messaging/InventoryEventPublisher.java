package com.adamdev.orderplatform.inventory.messaging;

import com.adamdev.orderplatform.events.StockFailedEvent;
import com.adamdev.orderplatform.events.StockReservedEvent;
import com.adamdev.orderplatform.events.Topics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishStockReserved(StockReservedEvent event) {
        send(Topics.STOCK_RESERVED, event.orderId().toString(), event);
    }

    public void publishStockFailed(StockFailedEvent event) {
        send(Topics.STOCK_FAILED, event.orderId().toString(), event);
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