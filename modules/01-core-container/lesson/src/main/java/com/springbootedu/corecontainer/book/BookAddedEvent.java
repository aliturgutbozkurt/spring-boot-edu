package com.springbootedu.corecontainer.book;

/**
 * Lesson 3.7 — any object can be an application event; a record keeps it immutable.
 */
public record BookAddedEvent(Book book) {
}
