package com.adamdev.orderplatform.order.service;

import com.adamdev.orderplatform.order.dto.CreateOrderRequest;
import com.adamdev.orderplatform.order.dto.OrderItemRequest;
import com.adamdev.orderplatform.order.dto.OrderResponse;
import com.adamdev.orderplatform.order.exception.OrderNotFoundException;
import com.adamdev.orderplatform.order.mapper.OrderMapper;
import com.adamdev.orderplatform.order.model.Order;
import com.adamdev.orderplatform.order.model.OrderItem;
import com.adamdev.orderplatform.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(
            OrderRepository orderRepository,
            OrderMapper orderMapper
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        Order order = new Order(request.customerId(), request.paymentMethodToken());
        for (OrderItemRequest itemRequest : request.items()) {
            order.addItem(new OrderItem(itemRequest.productId(), itemRequest.quantity()));
        }
        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }


    @Transactional(readOnly = true)
    public OrderResponse getById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
