package com.adamdev.orderplatform.order.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String paymentMethodToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    private Long totalAmountCents;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();


    public Order(String customerId, String paymentMethodToken) {
        this.customerId = customerId;
        this.paymentMethodToken = paymentMethodToken;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void markConfirmed(long totalAmountCents) {
        this.status = OrderStatus.CONFIRMED;
        this.totalAmountCents = totalAmountCents;
    }

    public void markCancelled() {
        this.status = OrderStatus.CANCELLED;
    }

}
