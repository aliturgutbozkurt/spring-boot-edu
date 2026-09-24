package com.springbootedu.modulith.loyalty;

import com.springbootedu.modulith.order.OrderPlaced;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Exercise 2 — turns every OrderPlaced into points.
 */
@Component
class PointsForOrders {

    private final LoyaltyPoints loyalty;

    PointsForOrders(LoyaltyPoints loyalty) {
        this.loyalty = loyalty;
    }

    @ApplicationModuleListener
    void on(OrderPlaced order) {
        int points = order.total().divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValue();
        loyalty.add(order.customerId(), points);
    }
}
