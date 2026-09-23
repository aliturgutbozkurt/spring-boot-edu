package com.springbootedu.messagingkafka.exercise2;

import com.springbootedu.messagingkafka.events.OrderPlaced;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Given: takes payment for orders; rejects orders with a quantity of zero or less.
 */
@Component
public class PaymentListener {

    public static final String TOPIC = "ex2-orders";

    private final List<OrderPlaced> paid = new CopyOnWriteArrayList<>();
    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();

    @KafkaListener(topics = TOPIC, groupId = "payments")
    public void onOrderPlaced(OrderPlaced order) {
        attempts.merge(order.orderId(), 1, Integer::sum);
        if (order.quantity() <= 0) {
            throw new InvalidQuantityException(order.quantity());
        }
        paid.add(order);
    }

    public List<OrderPlaced> paid() {
        return List.copyOf(paid);
    }

    public int attempts(String orderId) {
        return attempts.getOrDefault(orderId, 0);
    }
}
