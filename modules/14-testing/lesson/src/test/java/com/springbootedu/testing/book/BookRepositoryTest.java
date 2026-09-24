package com.springbootedu.testing.book;

import static com.springbootedu.testing.fixtures.TestBooks.aBook;
import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.testing.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

/**
 * Lesson 3.4 — a JPA slice test against real PostgreSQL. Every test runs in a transaction that is rolled back.
 */
// tag::data-slice[]
@DataJpaTest
@Import(TestcontainersConfiguration.class)
class BookRepositoryTest {

    @Autowired
    BookRepository books;

    @Autowired
    TestEntityManager entities;                    // writes test data directly, bypassing the repository

    @Test
    void findsABookByIsbn() {
        entities.persistAndFlush(aBook().isbn("9789999999991").build());

        assertThat(books.findByIsbn("9789999999991")).isPresent();
    }

    @Test
    void listsTheBooksRunningLowWithTheLowestStockFirst() {
        entities.persist(aBook().isbn("9789999999992").stock(1).build());
        entities.persist(aBook().isbn("9789999999993").soldOut().build());
        entities.persistAndFlush(aBook().isbn("9789999999994").stock(50).build());

        assertThat(books.runningLow(2)).extracting(Book::getIsbn)
                .containsSequence("9789999999993", "9789999999992")    // and the seeded sold-out Java Puzzlers
                .doesNotContain("9789999999994");
    }
}
// end::data-slice[]
