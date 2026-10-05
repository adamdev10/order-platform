package com.adamdev.orderplatform.inventory.mapper;

import com.adamdev.orderplatform.inventory.dto.ReservationItemResponse;
import com.adamdev.orderplatform.inventory.dto.ReservationResponse;
import com.adamdev.orderplatform.inventory.model.Reservation;
import com.adamdev.orderplatform.inventory.model.ReservationItem;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getOrderId(),
                reservation.getStatus(),
                reservation.getTotalAmountCents(),
                reservation.getCreatedAt(),
                reservation.getItems().stream()
                        .map(this::toItemResponse)
                        .toList()
        );
    }

    public ReservationItemResponse toItemResponse(ReservationItem item) {
        return new ReservationItemResponse(
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPriceCents()
        );
    }
}