package com.springbootedu.nativeperformance.book;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.nativeperformance.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.context.annotation.Import;

/**
 * Lesson 3.2 — the repository behaves the same with and without AOT; the AOT version is generated code.
 */
@DataJdbcTest
@Import(TestcontainersConfiguration.class)
class BookRepositoryTest {

    @Autowired
    BookRepository books;

    @Test
    void aDerivedQuery() {
        assertThat(books.findByTitleContainingIgnoreCaseOrderByTitle("java"))
                .extracting(Book::title)
                .containsExactly("Effective Java", "Java Concurrency in Practice", "Java Puzzlers");
    }

    @Test
    void aDeclaredQuery() {
        assertThat(books.publishedSince(2017))
                .extracting(Book::title)
                .containsExactly("Designing Data-Intensive Applications", "Effective Java", "Spring in Action");
    }
}
