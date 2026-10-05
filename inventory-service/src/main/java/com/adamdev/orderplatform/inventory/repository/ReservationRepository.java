package com.adamdev.orderplatform.inventory.repository;

import com.adamdev.orderplatform.inventory.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    Optional<Reservation> findByOrderId(UUID orderId);

    boolean existsByOrderId(UUID orderId);
}