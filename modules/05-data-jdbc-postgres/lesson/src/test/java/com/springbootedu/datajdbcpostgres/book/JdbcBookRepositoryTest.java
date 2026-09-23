package com.springbootedu.datajdbcpostgres.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.datajdbcpostgres.TestcontainersConfiguration;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Lesson 3.3 — JdbcClient. Each @JdbcTest runs in a transaction that is rolled back afterwards.
 */
@JdbcTest
@Import({TestcontainersConfiguration.class, JdbcBookRepository.class})
class JdbcBookRepositoryTest {

    @Autowired
    JdbcBookRepository books;

    @Test
    void mapsRowsToRecords() {
        assertThat(books.findAll()).hasSize(5).first()
                .isEqualTo(new Book(1L, "9780134685991", "Effective Java", new BigDecimal("89.90"), 5));
    }

    @Test
    void findsByIsbnWithANamedParameter() {
        assertThat(books.findByIsbn("9780321336781")).map(Book::title).contains("Java Puzzlers");
        assertThat(books.findByIsbn("9780000000000")).isEmpty();
    }

    @Test
    void joinsIntoAViewRecord() {
        assertThat(books.findWithAuthors())
                .contains(new BookWithAuthor("Refactoring", "Martin Fowler"));
    }

    @Test
    void insertsAndReturnsTheGeneratedId() {
        long id = books.insert(new Book(null, "9780132350884", "Clean Code", new BigDecimal("75.50"), 3));

        assertThat(id).isGreaterThan(5);
        assertThat(books.findByIsbn("9780132350884")).map(Book::id).contains(id);
    }

    @Test
    void updatesStock() {
        assertThat(books.changeStock("9780134685991", -2)).isEqualTo(1);    // one row changed
        assertThat(books.findByIsbn("9780134685991")).map(Book::stock).contains(3);
    }

    @Test
    void theCheckConstraintRejectsNegativeStock() {
        assertThatThrownBy(() -> books.changeStock("9780321336781", -100))
                .isInstanceOf(DataIntegrityViolationException.class);       // SQL error → Spring's exception hierarchy
    }
}
