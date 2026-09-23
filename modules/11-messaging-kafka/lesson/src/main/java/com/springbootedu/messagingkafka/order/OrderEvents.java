package com.springbootedu.messagingkafka.order;

import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.1 — the producer. The order id is the key: all events of one order go to the same partition, in order.
 */
// tag::producer[]
@Component
public class OrderEvents {

    public static final String TOPIC = "orders";
    public static final String DEAD_LETTER_TOPIC = "orders.DLT";

    private final KafkaTemplate<String, OrderPlaced> kafka;

    public OrderEvents(KafkaTemplate<String, OrderPlaced> kafka) {
        this.kafka = kafka;
    }

    public CompletableFuture<SendResult<String, OrderPlaced>> publish(OrderPlaced order) {
        return kafka.send(TOPIC, order.orderId(), order);                // asynchronous: returns at once
    }
}
// end::producer[]
