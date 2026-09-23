package com.springbootedu.corecontainer.event;

import com.springbootedu.corecontainer.book.BookAddedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.7 — a conditional listener: the SpEL condition filters events before the method is called.
 */
// tag::conditional-listener[]
@Component
public class PremiumBookListener {

    private final NotificationLog log;

    public PremiumBookListener(NotificationLog log) {
        this.log = log;
    }

    @EventListener(condition = "#event.book().price() >= 100")
    @Order(2)
    public void onPremiumBook(BookAddedEvent event) {
        log.add("Premium kitap / Premium book: " + event.book().title());
    }
}
// end::conditional-listener[]
