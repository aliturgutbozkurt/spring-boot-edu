package com.springbootedu.nativeperformance.book;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

/**
 * Lesson 3.2 — with AOT, Spring Data generates the implementation of these methods at build time
 * (BookRepositoryImpl__AotRepository) instead of deriving the queries at startup.
 */
// tag::repository[]
public interface BookRepository extends ListCrudRepository<Book, String> {

    List<Book> findByTitleContainingIgnoreCaseOrderByTitle(String part);

    @Query("SELECT * FROM book WHERE year >= :year ORDER BY title")
    List<Book> publishedSince(int year);
}
// end::repository[]
