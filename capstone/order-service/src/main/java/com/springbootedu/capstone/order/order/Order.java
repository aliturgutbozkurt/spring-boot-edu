package com.springbootedu.capstone.order.order;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
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

    @ElementCollection(fetch = FetchType.EAGER)                 // an order is always read with its lines
    @CollectionTable(name = "order_lines", joinColumns = @JoinColumn(name = "order_id"))
    @OrderColumn(name = "line_no")
    private List<OrderLine> lines = new ArrayList<>();

    protected Order() {                                         // for JPA
    }

    Order(UUID id, String customerId, List<OrderLine> lines, Instant placedAt) {
        this.id = id;
        this.customerId = customerId;
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
}
