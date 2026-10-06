package com.adamdev.orderplatform.payment.controller;

import com.adamdev.orderplatform.payment.dto.PaymentRequest;
import com.adamdev.orderplatform.payment.dto.PaymentResponse;
import com.adamdev.orderplatform.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> charge(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.charge(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}