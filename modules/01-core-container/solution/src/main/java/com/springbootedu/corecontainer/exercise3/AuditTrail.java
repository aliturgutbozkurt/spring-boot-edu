package com.springbootedu.corecontainer.exercise3;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Exercise 3 — given: records every published book. Runs with order 10.
 */
@Component
public class AuditTrail {

    private final Inbox inbox;

    public AuditTrail(Inbox inbox) {
        this.inbox = inbox;
    }

    @EventListener
    @Order(10)
    public void onBookAdded(BookAddedEvent event) {
        inbox.add("audit: " + event.isbn());
    }
}
