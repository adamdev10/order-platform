package com.adamdev.orderplatform.inventory.messaging;

import com.adamdev.orderplatform.events.OrderCreatedEvent;
import com.adamdev.orderplatform.events.StockFailedEvent;
import com.adamdev.orderplatform.events.StockReservedEvent;
import com.adamdev.orderplatform.events.StockReservedItem;
import com.adamdev.orderplatform.events.Topics;
import com.adamdev.orderplatform.inventory.dto.ReservationItemRequest;
import com.adamdev.orderplatform.inventory.dto.ReservationRequest;
import com.adamdev.orderplatform.inventory.dto.ReservationResponse;
import com.adamdev.orderplatform.inventory.exception.InsufficientStockException;
import com.adamdev.orderplatform.inventory.exception.ProductNotFoundException;
import com.adamdev.orderplatform.inventory.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class OrderCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    private final InventoryService inventoryService;
    private final InventoryEventPublisher eventPublisher;

    public OrderCreatedListener(
            InventoryService inventoryService,
            InventoryEventPublisher eventPublisher
    ) {
        this.inventoryService = inventoryService;
        this.eventPublisher = eventPublisher;
    }

    @KafkaListener(topics = Topics.ORDER_CREATED)
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Received order-created for order {}", event.orderId());

        try {
            ReservationRequest request = new ReservationRequest(
                    event.orderId(),
                    event.items().stream()
                            .map(i -> new ReservationItemRequest(i.productId(), i.quantity()))
                            .toList()
            );

            ReservationResponse reservation = inventoryService.reserve(request);

            StockReservedEvent stockReserved = new StockReservedEvent(
                    reservation.orderId(),
                    reservation.reservationId(),
                    event.customerId(),
                    event.paymentMethodToken(),
                    toStockReservedItems(reservation),
                    reservation.totalAmountCents(),
                    "EUR",
                    Instant.now()
            );
            eventPublisher.publishStockReserved(stockReserved);
            log.info("Stock reserved for order {}, total {}",
                    reservation.orderId(), reservation.totalAmountCents());

        } catch (ProductNotFoundException ex) {
            publishFailure(event, "PRODUCT_NOT_FOUND", ex.getMessage(), 0, 0);
        } catch (InsufficientStockException ex) {
            publishFailure(event, "INSUFFICIENT_STOCK", ex.getMessage(),
                    ex.getRequested(), ex.getAvailable());
        }
    }

    private void publishFailure(OrderCreatedEvent event, String reason,
                                String productId, int requested, int available) {
        log.warn("Stock failed for order {}: {}", event.orderId(), reason);
        StockFailedEvent failed = new StockFailedEvent(
                event.orderId(),
                reason,
                productId,
                requested,
                available,
                Instant.now()
        );
        eventPublisher.publishStockFailed(failed);
    }

    private List<StockReservedItem> toStockReservedItems(ReservationResponse reservation) {
        return reservation.items().stream()
                .map(i -> new StockReservedItem(
                        i.productId(),
                        i.quantity(),
                        i.unitPriceCents()
                ))
                .toList();
    }
}