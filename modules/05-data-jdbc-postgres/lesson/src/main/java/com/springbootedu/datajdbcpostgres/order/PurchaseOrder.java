package com.springbootedu.datajdbcpostgres.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Lesson 3.4 — the aggregate root. Spring Data JDBC saves and loads it together with its lines.
 */
// tag::aggregate[]
@Table("purchase_order")                                   // "order" is a reserved word in SQL
public record PurchaseOrder(
        @Id @Nullable Long id,                             // null → INSERT, otherwise UPDATE
        String customerEmail,
        Instant createdAt,
        @MappedCollection(idColumn = "purchase_order") Set<OrderLine> lines) {

    public static PurchaseOrder create(String customerEmail, Set<OrderLine> lines) {
        return new PurchaseOrder(null, customerEmail, Instant.now(), lines);
    }

    public PurchaseOrder withId(Long newId) {              // Spring Data uses this to set the generated id
        return new PurchaseOrder(newId, customerEmail, createdAt, lines);
    }
    // end::aggregate[]

    public BigDecimal total() {
        return lines.stream().map(OrderLine::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
