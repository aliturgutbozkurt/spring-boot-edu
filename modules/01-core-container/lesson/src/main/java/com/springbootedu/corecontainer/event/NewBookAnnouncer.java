package com.springbootedu.corecontainer.event;

import com.springbootedu.corecontainer.book.BookAddedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.7 — a listener: the method parameter type selects the events it receives.
 */
// tag::event-listener[]
@Component
public class NewBookAnnouncer {

    private final NotificationLog log;

    public NewBookAnnouncer(NotificationLog log) {
        this.log = log;
    }

    @EventListener
    @Order(1)                                 // lower value → called earlier
    public void announce(BookAddedEvent event) {
        log.add("Yeni kitap / New book: " + event.book().title());
    }
}
// end::event-listener[]
