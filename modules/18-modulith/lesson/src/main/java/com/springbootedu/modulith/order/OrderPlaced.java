package com.springbootedu.modulith.order;

import java.math.BigDecimal;
import org.springframework.modulith.events.Externalized;

/**
 * Lessons 3.2 and 3.4 — the event other modules react to. Only data, no entities: listeners run later,
 * in other transactions, and the event is stored as JSON in the publication registry.
 */
// tag::event[]
@Externalized("bookstore.orders::#{customerId()}")      // also send to the Kafka topic, key = customer
public record OrderPlaced(long orderId, String customerId, String isbn, int quantity, BigDecimal total) {
}
// end::event[]
