package com.springbootedu.datajpapostgres.exercise3;

import com.springbootedu.datajpapostgres.exercise1.OrderLine;
import com.springbootedu.datajpapostgres.exercise1.OrderStatus;
import com.springbootedu.datajpapostgres.exercise1.PurchaseOrder;
import jakarta.persistence.criteria.Join;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

/**
 * Exercise 3 — building blocks of the order search.
 */
final class OrderSpecifications {

    private OrderSpecifications() {
    }

    static Specification<PurchaseOrder> customer(String email) {
        return (order, query, cb) -> cb.equal(order.get("customerEmail"), email);
    }

    static Specification<PurchaseOrder> status(OrderStatus status) {
        return (order, query, cb) -> cb.equal(order.get("status"), status);
    }

    static Specification<PurchaseOrder> createdAfter(Instant instant) {
        return (order, query, cb) -> cb.greaterThan(order.get("createdAt"), instant);
    }

    static Specification<PurchaseOrder> containsIsbn(String isbn) {
        return (order, query, cb) -> {
            query.distinct(true);                                  // two matching lines must not duplicate the order
            Join<PurchaseOrder, OrderLine> lines = order.join("lines");
            return cb.equal(lines.get("isbn"), isbn);
        };
    }
}
