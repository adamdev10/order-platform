package com.adamdev.orderplatform.inventory.messaging;

import com.adamdev.orderplatform.events.StockReleaseEvent;
import com.adamdev.orderplatform.events.Topics;
import com.adamdev.orderplatform.inventory.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StockReleaseListener {

    private static final Logger log = LoggerFactory.getLogger(StockReleaseListener.class);

    private final InventoryService inventoryService;

    public StockReleaseListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = Topics.STOCK_RELEASE)
    public void onStockRelease(StockReleaseEvent event) {
        log.info("Received stock-release for order {}", event.orderId());
        inventoryService.release(event.orderId());
    }
}