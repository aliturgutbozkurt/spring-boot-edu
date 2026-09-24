package com.springbootedu.modulith.notification;

import com.springbootedu.modulith.order.OrderPlaced;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.3 — a second listener of the same event, in another module. The order module knows neither.
 */
@Component
class OrderConfirmation {

    private final Mailbox mailbox;

    OrderConfirmation(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    @ApplicationModuleListener
    void on(OrderPlaced order) {
        mailbox.send(order.customerId(), "Thank you for order " + order.orderId() + ": " + order.quantity()
                                         + " × " + order.isbn() + ", total " + order.total());
    }
}
