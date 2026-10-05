package com.adamdev.orderplatform.order.controller;

import com.adamdev.orderplatform.order.dto.CreateOrderRequest;
import com.adamdev.orderplatform.order.dto.OrderResponse;
import com.adamdev.orderplatform.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable UUID id) {
        return orderService.getById(id);
    }
}
