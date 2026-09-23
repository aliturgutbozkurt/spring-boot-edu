package com.springbootedu.setupmodernjava.exercise3;

import java.math.BigDecimal;

/**
 * Exercise 3 — the allowed transitions of an order.
 */
public final class OrderStateMachine {

    private record Transition(OrderState state, OrderEvent event) {
    }

    private OrderStateMachine() {
    }

    public static OrderState next(OrderState state, OrderEvent event) {
        return switch (new Transition(state, event)) {
            case Transition(New _, Pay(BigDecimal amount)) when amount.signum() > 0 -> new Paid(amount);
            case Transition(Paid _, Ship(String trackingNumber)) -> new Shipped(trackingNumber);
            case Transition(Shipped _, Deliver _) -> new Delivered();
            case Transition(New _, Cancel(String reason)) -> new Cancelled(reason);
            case Transition(Paid _, Cancel(String reason)) -> new Cancelled(reason);
            case Transition t -> throw new IllegalStateException(
                    "Cannot " + t.event().getClass().getSimpleName() + " when " + t.state().getClass().getSimpleName());
        };
    }
}
