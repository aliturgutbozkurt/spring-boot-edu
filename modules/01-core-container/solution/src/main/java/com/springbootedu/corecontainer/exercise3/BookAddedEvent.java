package com.springbootedu.corecontainer.exercise3;

/**
 * Exercise 3 — given: the event published when a new book is added.
 */
public record BookAddedEvent(String isbn, String title, String author) {
}
