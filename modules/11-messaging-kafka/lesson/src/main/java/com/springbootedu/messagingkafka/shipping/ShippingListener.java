package com.springbootedu.messagingkafka.shipping;

import com.springbootedu.messagingkafka.order.OrderEvents;
import com.springbootedu.messagingkafka.order.OrderPlaced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.2 — a consumer. All instances with groupId "shipping" share the partitions of the topic.
 */
// tag::consumer[]
@Component
class ShippingListener {

    private static final Logger log = LoggerFactory.getLogger(ShippingListener.class);

    private final Shipments shipments;

    ShippingListener(Shipments shipments) {
        this.shipments = shipments;
    }

    @KafkaListener(topics = OrderEvents.TOPIC, groupId = "shipping")
    void onOrderPlaced(OrderPlaced order) {                             // JSON → record by the deserializer
        if (order.quantity() <= 0) {
            throw new InvalidOrderException("quantity must be positive, was " + order.quantity());
        }
        shipments.ship(order);
        log.info("Shipping {} × {} for order {}", order.quantity(), order.isbn(), order.orderId());
    }
}
// end::consumer[]
