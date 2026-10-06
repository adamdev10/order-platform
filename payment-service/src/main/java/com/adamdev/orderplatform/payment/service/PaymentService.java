package com.adamdev.orderplatform.payment.service;

import com.adamdev.orderplatform.payment.dto.PaymentRequest;
import com.adamdev.orderplatform.payment.dto.PaymentResponse;
import com.adamdev.orderplatform.payment.exception.PaymentAlreadyProcessedException;
import com.adamdev.orderplatform.payment.gateway.PaymentGateway;
import com.adamdev.orderplatform.payment.gateway.PaymentResult;
import com.adamdev.orderplatform.payment.mapper.PaymentMapper;
import com.adamdev.orderplatform.payment.model.Payment;
import com.adamdev.orderplatform.payment.model.PaymentStatus;
import com.adamdev.orderplatform.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final PaymentMapper paymentMapper;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentGateway paymentGateway,
            PaymentMapper paymentMapper
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
        this.paymentMapper = paymentMapper;
    }

    @Transactional
    public PaymentResponse charge(PaymentRequest request) {
        if (paymentRepository.existsByOrderId(request.orderId())) {
            log.warn("Payment already exists for order {}", request.orderId());
            throw new PaymentAlreadyProcessedException(request.orderId());
        }

        PaymentResult result = paymentGateway.charge(
                request.paymentMethodToken(),
                request.amountCents(),
                request.currency()
        );

        Payment payment;
        if (result.success()) {
            payment = Payment.completed(
                    request.orderId(),
                    request.customerId(),
                    request.amountCents(),
                    request.currency(),
                    result.transactionId()
            );
        } else {
            payment = Payment.failed(
                    request.orderId(),
                    request.customerId(),
                    request.amountCents(),
                    request.currency(),
                    result.failureReason()
            );
        }

        Payment saved = paymentRepository.save(payment);

        if (saved.getStatus() == PaymentStatus.COMPLETED) {
            log.info("Payment completed for order {} txn={}",
                    saved.getOrderId(), saved.getTransactionId());
        } else {
            log.warn("Payment failed for order {} reason={}",
                    saved.getOrderId(), saved.getFailureReason());
        }

        return paymentMapper.toResponse(saved);
    }
}