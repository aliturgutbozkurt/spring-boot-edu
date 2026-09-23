package com.springbootedu.datajpapostgres.exercise1;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import org.jspecify.annotations.Nullable;

/**
 * Exercise 1 — a line belongs to exactly one order (the owning side of the relationship).
 */
@Entity
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id;

    // TODO 1b: replace @Transient with a lazy many-to-one mapping to the column "order_id"
    @Transient
    private PurchaseOrder order;

    private String isbn;
    private int quantity;
    private BigDecimal unitPrice;

    protected OrderLine() {
        this.order = new PurchaseOrder("");
        this.isbn = "";
        this.unitPrice = BigDecimal.ZERO;
    }

    OrderLine(PurchaseOrder order, String isbn, int quantity, BigDecimal unitPrice) {
        this.order = order;
        this.isbn = isbn;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public @Nullable Long getId() {
        return id;
    }

    public PurchaseOrder getOrder() {
        return order;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
