package com.springbootedu.messagingkafka.exercise3;

import com.springbootedu.messagingkafka.events.OrderPlaced;
import java.util.UUID;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

/**
 * Given: places an order by writing its event to the outbox table (the order table is left out for brevity).
 */
@Service
public class OrderDesk {

    private final JdbcClient jdbc;
    private final JsonMapper json;

    public OrderDesk(JdbcClient jdbc, JsonMapper json) {
        this.jdbc = jdbc;
        this.json = json;
    }

    @Transactional
    public String place(String customerId, int quantity) {
        OrderPlaced event = new OrderPlaced(UUID.randomUUID().toString(), "9780134685991", quantity);
        jdbc.sql("INSERT INTO outbox (aggregate_id, payload) VALUES (:id, :payload)")
                .param("id", event.orderId())
                .param("payload", json.writeValueAsString(event))
                .update();
        return event.orderId();
    }
}
