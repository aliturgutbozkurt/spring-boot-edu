package com.springbootedu.capstone.order.order;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/**
 * A placed order. The ID is created by the service (not the database), because the catalog reservation
 * needs it before the order is saved.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;

    private String customerId;

    private BigDecimal total;

    private Instant placedAt;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PLACED;

    private @Nullable String idempotencyKey;                    // exercise 1: the client's key, if it sent one

    @ElementCollection(fetch = FetchType.EAGER)                 // an order is always read with its lines
    @CollectionTable(name = "order_lines", joinColumns = @JoinColumn(name = "order_id"))
    @OrderColumn(name = "line_no")
    private List<OrderLine> lines = new ArrayList<>();

    protected Order() {                                         // for JPA
    }

    public enum Status { PLACED, CANCELLED }

    Order(UUID id, String customerId, List<OrderLine> lines, Instant placedAt, @Nullable String idempotencyKey) {
        this.id = id;
        this.customerId = customerId;
        this.idempotencyKey = idempotencyKey;
        this.lines = new ArrayList<>(lines);
        this.total = lines.stream().map(OrderLine::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.placedAt = placedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Instant getPlacedAt() {
        return placedAt;
    }

    public List<OrderLine> getLines() {
        return List.copyOf(lines);
    }

    public Status getStatus() {
        return status;
    }

    /** Exercise 2 — an order can be cancelled once. */
    void cancel() {
        if (status == Status.CANCELLED) {
            throw new OrderAlreadyCancelledException(id.toString());
        }
        status = Status.CANCELLED;
    }
}
