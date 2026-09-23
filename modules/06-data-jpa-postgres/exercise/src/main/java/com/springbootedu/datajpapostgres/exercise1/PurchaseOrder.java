package com.springbootedu.datajpapostgres.exercise1;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * Exercise 1 — the aggregate root: its lines are created, saved and deleted through it.
 */
@Entity
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id;

    private String customerEmail;

    @Enumerated(EnumType.STRING)                  // store "PAID", not 1 — safe when the enum changes
    private OrderStatus status;

    private Instant createdAt;

    // TODO 1a: replace @Transient with the one-to-many mapping of this aggregate:
    //          the lines are owned by OrderLine.order, saved together with the order,
    //          and a line removed from this list must be deleted from the database
    @Transient
    private List<OrderLine> lines = new ArrayList<>();

    protected PurchaseOrder() {
        this("");
    }

    public PurchaseOrder(String customerEmail) {
        this.customerEmail = customerEmail;
        this.status = OrderStatus.NEW;
        this.createdAt = Instant.now();
    }

    public void addLine(String isbn, int quantity, BigDecimal unitPrice) {
        // TODO 1c: create an OrderLine that points to THIS order and add it to the list
        throw new UnsupportedOperationException("TODO 1c");
    }

    public void removeLine(OrderLine line) {
        // TODO 1c: remove the line from the list (with the right mapping, that deletes the row)
        throw new UnsupportedOperationException("TODO 1c");
    }

    public BigDecimal total() {
        return lines.stream().map(OrderLine::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public @Nullable Long getId() {
        return id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<OrderLine> getLines() {
        return lines;
    }
}
