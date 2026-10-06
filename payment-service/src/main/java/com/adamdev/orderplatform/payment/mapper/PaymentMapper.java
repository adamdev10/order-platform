package com.adamdev.orderplatform.payment.mapper;

import com.adamdev.orderplatform.payment.dto.PaymentResponse;
import com.adamdev.orderplatform.payment.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getStatus(),
                payment.getAmountCents(),
                payment.getCurrency(),
                payment.getTransactionId(),
                payment.getFailureReason(),
                payment.getCreatedAt()
        );
    }
}