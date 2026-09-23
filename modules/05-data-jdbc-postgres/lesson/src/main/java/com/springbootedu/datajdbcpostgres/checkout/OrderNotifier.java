package com.springbootedu.datajdbcpostgres.checkout;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Lesson 3.6 — reacts only if the checkout really committed; a rolled-back order never notifies anyone.
 */
// tag::after-commit[]
@Component
public class OrderNotifier {

    private final JdbcClient jdbc;

    public OrderNotifier(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)   // the default phase
    @Transactional(propagation = Propagation.REQUIRES_NEW)              // the original transaction is over
    public void onOrderPlaced(OrderPlacedEvent event) {
        jdbc.sql("insert into notification (message) values (?)")
                .param("Siparişiniz alındı / Order received: #" + event.orderId() + " → " + event.customerEmail())
                .update();
    }
}
// end::after-commit[]
