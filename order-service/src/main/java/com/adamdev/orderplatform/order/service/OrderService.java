package com.adamdev.orderplatform.order.service;

import com.adamdev.orderplatform.events.OrderCreatedEvent;
import com.adamdev.orderplatform.events.OrderCreatedItem;
import com.adamdev.orderplatform.order.dto.CreateOrderRequest;
import com.adamdev.orderplatform.order.dto.OrderItemRequest;
import com.adamdev.orderplatform.order.dto.OrderResponse;
import com.adamdev.orderplatform.order.exception.OrderNotFoundException;
import com.adamdev.orderplatform.order.mapper.OrderMapper;
import com.adamdev.orderplatform.order.messaging.OrderEventPublisher;
import com.adamdev.orderplatform.order.model.Order;
import com.adamdev.orderplatform.order.model.OrderItem;
import com.adamdev.orderplatform.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderEventPublisher eventPublisher;

    public OrderService(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            OrderEventPublisher eventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        Order order = new Order(request.customerId(), request.paymentMethodToken());
        for (OrderItemRequest itemRequest : request.items()) {
            order.addItem(new OrderItem(itemRequest.productId(), itemRequest.quantity()));
        }

        Order saved = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                saved.getId(),
                saved.getCustomerId(),
                saved.getPaymentMethodToken(),
                toEventItems(saved),
                saved.getCreatedAt()
        );
        eventPublisher.publishOrderCreated(event);

        return orderMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Transactional
    public void markConfirmed(UUID orderId, long totalAmountCents) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.markConfirmed(totalAmountCents);
    }

    @Transactional
    public void markCancelled(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.markCancelled();
    }

    @Transactional(readOnly = true)
    public Order loadById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private List<OrderCreatedItem> toEventItems(Order order) {
        return order.getItems().stream()
                .map(item -> new OrderCreatedItem(
                        item.getProductId(),
                        item.getQuantity()
                ))
                .toList();
    }
}