package com.springbootedu.datajpapostgres.exercise3;

import com.springbootedu.datajpapostgres.exercise1.OrderStatus;
import com.springbootedu.datajpapostgres.exercise1.PurchaseOrder;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

/**
 * Exercise 3 — building blocks of the order search.
 */
final class OrderSpecifications {

    private OrderSpecifications() {
    }

    static Specification<PurchaseOrder> customer(String email) {
        // TODO 3a: customerEmail equals email
        throw new UnsupportedOperationException("TODO 3a");
    }

    static Specification<PurchaseOrder> status(OrderStatus status) {
        // TODO 3a: status equals status
        throw new UnsupportedOperationException("TODO 3a");
    }

    static Specification<PurchaseOrder> createdAfter(Instant instant) {
        // TODO 3a: createdAt is after instant
        throw new UnsupportedOperationException("TODO 3a");
    }

    static Specification<PurchaseOrder> containsIsbn(String isbn) {
        // TODO 3b: the order has at least one line with this isbn — each order only once in the result
        throw new UnsupportedOperationException("TODO 3b");
    }
}
