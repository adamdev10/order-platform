package com.adamdev.orderplatform.order.mapper;


import com.adamdev.orderplatform.order.dto.OrderItemResponse;
import com.adamdev.orderplatform.order.dto.OrderResponse;
import com.adamdev.orderplatform.order.model.Order;
import com.adamdev.orderplatform.order.model.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmountCents(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(this::toItemResponse)
                        .toList()
        );
    }

    public OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(item.getProductId(), item.getQuantity());
    }
}
