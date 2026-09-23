package com.springbootedu.webmvc.book;

import java.util.List;
import java.util.Optional;

/**
 * Storage abstraction; module 06 replaces the in-memory version with Spring Data JPA.
 */
public interface BookRepository {

    List<Book> findAll();

    Optional<Book> findById(long id);

    Book save(Book book);

    boolean deleteById(long id);
}
