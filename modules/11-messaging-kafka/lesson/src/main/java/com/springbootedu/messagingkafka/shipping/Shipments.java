package com.springbootedu.messagingkafka.shipping;

import com.springbootedu.messagingkafka.order.OrderPlaced;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.2 — remembers what was shipped (a stand-in for a real shipping system).
 */
@Component
public class Shipments {

    private final List<OrderPlaced> shipped = new CopyOnWriteArrayList<>();

    void ship(OrderPlaced order) {
        shipped.add(order);
    }

    public List<OrderPlaced> shipped() {
        return List.copyOf(shipped);
    }
}
