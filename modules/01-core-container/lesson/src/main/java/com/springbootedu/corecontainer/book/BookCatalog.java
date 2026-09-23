package com.springbootedu.corecontainer.book;

import java.util.List;

/**
 * Lesson 3.1 — the abstraction that {@link BookService} depends on.
 */
public interface BookCatalog {

    List<Book> findAll();

    void save(Book book);
}
