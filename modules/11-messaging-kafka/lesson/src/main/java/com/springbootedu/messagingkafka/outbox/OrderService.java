package com.springbootedu.messagingkafka.outbox;

import com.springbootedu.messagingkafka.order.OrderPlaced;
import java.util.UUID;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

/**
 * Lesson 3.6 — places an order. The event is not sent to Kafka here: it is stored in the outbox table,
 * in the same database transaction as the order itself.
 */
// tag::outbox-write[]
@Service
public class OrderService {

    private final JdbcClient jdbc;
    private final JsonMapper json;

    public OrderService(JdbcClient jdbc, JsonMapper json) {
        this.jdbc = jdbc;
        this.json = json;
    }

    @Transactional
    public OrderPlaced place(String customerId, String isbn, int quantity) {
        OrderPlaced event = new OrderPlaced(UUID.randomUUID().toString(), customerId, isbn, quantity);

        jdbc.sql("INSERT INTO orders (id, customer_id, isbn, quantity) VALUES (:id, :customer, :isbn, :quantity)")
                .param("id", event.orderId()).param("customer", customerId)
                .param("isbn", isbn).param("quantity", quantity)
                .update();
        jdbc.sql("INSERT INTO outbox (aggregate_id, event_type, payload) VALUES (:id, :type, :payload)")
                .param("id", event.orderId())
                .param("type", OrderPlaced.class.getSimpleName())
                .param("payload", json.writeValueAsString(event))
                .update();
        reserveStock(isbn, quantity);                   // may fail → the order AND the event are rolled back
        return event;
    }
    // end::outbox-write[]

    private void reserveStock(String isbn, int quantity) {
        int updated = jdbc.sql("UPDATE stock SET available = available - :q WHERE isbn = :isbn AND available >= :q")
                .param("q", quantity).param("isbn", isbn)
                .update();
        if (updated == 0) {
            throw new OutOfStockException(isbn, quantity);
        }
    }
}
