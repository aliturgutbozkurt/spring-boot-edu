package com.springbootedu.datajdbcpostgres.order;

import java.math.BigDecimal;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Lesson 3.4 — part of the PurchaseOrder aggregate; it has no repository of its own.
 */
@Table("order_line")
public record OrderLine(String isbn, int quantity, BigDecimal unitPrice) {

    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
