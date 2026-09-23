package com.springbootedu.corecontainer.exercise3;

import org.springframework.stereotype.Service;

/**
 * Exercise 3 — publishes a {@link BookAddedEvent}; it does not know who listens.
 */
@Service
public class BookPublisher {

    // TODO 3a: receive an ApplicationEventPublisher through the constructor and keep it in a field

    public void publish(String isbn, String title, String author) {
        // TODO 3b: publish a new BookAddedEvent(isbn, title, author)
    }
}
