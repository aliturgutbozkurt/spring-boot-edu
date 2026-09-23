package com.springbootedu.setupmodernjava.exercise3;

/**
 * Exercise 3 — the allowed transitions of an order.
 */
public final class OrderStateMachine {

    private OrderStateMachine() {
    }

    public static OrderState next(OrderState state, OrderEvent event) {
        // TODO 3a: allowed transitions
        //          New     + Pay(amount > 0)   → Paid(amount)
        //          Paid    + Ship(tracking)    → Shipped(tracking)
        //          Shipped + Deliver           → Delivered
        //          New or Paid + Cancel(reason) → Cancelled(reason)
        // TODO 3b: anything else → IllegalStateException("Cannot <Event> when <State>"),
        //          e.g. "Cannot Ship when New" (hint: getClass().getSimpleName())
        // Hint: switch over a small private record Transition(OrderState state, OrderEvent event)
        //       and use nested record patterns.
        throw new UnsupportedOperationException("TODO 3");
    }
}
