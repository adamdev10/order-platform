package com.adamdev.orderplatform.inventory.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private long totalAmountCents;

    @Column(nullable = false)
    private Instant createdAt;

    @OneToMany(
            mappedBy = "reservation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ReservationItem> items = new ArrayList<>();

    public Reservation(UUID orderId) {
        this.orderId = orderId;
        this.status = ReservationStatus.RESERVED;
        this.totalAmountCents = 0L;
        this.createdAt = Instant.now();
    }

    public void addItem(ReservationItem item) {
        items.add(item);
        item.setReservation(this);
        this.totalAmountCents += (long) item.getQuantity() * item.getUnitPriceCents();
    }

    public void release() {
        this.status = ReservationStatus.RELEASED;
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }
}