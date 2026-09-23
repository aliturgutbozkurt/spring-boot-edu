package com.springbootedu.corecontainer.exercise3;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Exercise 3 — publishes a {@link BookAddedEvent}; it does not know who listens.
 */
@Service
public class BookPublisher {

    private final ApplicationEventPublisher events;

    public BookPublisher(ApplicationEventPublisher events) {
        this.events = events;
    }

    public void publish(String isbn, String title, String author) {
        events.publishEvent(new BookAddedEvent(isbn, title, author));
    }
}
