package com.springbootedu.testing.book;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Lesson 3.4 — a repository with a derived query and a JPQL query: worth a slice test against real PostgreSQL.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    @Query("select b from Book b where b.stock < :limit order by b.stock")
    List<Book> runningLow(int limit);
}
