package com.springbootedu.datajpapostgres.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.datajpapostgres.TestcontainersConfiguration;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Lessons 3.1, 3.2 and 3.4 — mapping, derived queries with paging, and projections.
 */
@DataJpaTest
@Import(TestcontainersConfiguration.class)
class BookRepositoryTest {

    @Autowired
    BookRepository books;

    @Test
    void mapsTheRelationships() {
        Book book = books.findByIsbn("9780134685991").orElseThrow();

        assertThat(book.getAuthor().getName()).isEqualTo("Joshua Bloch");
        assertThat(book.getCategories()).extracting(Category::getName).containsExactlyInAnyOrder("java", "best-practices");
    }

    @Test
    void derivedQueryWithPagingAndSorting() {
        Page<Book> page = books.findByTitleContainingIgnoreCase("java",
                PageRequest.of(0, 2, Sort.by("price").descending()));

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getContent()).extracting(Book::getTitle).containsExactly("Modern Java in Action", "Effective Java");
    }

    @Test
    void interfaceProjectionSelectsOnlyTheNeededColumns() {
        assertThat(books.findByAuthorNameOrderByTitle("Joshua Bloch"))
                .extracting(BookTitleAndPrice::getTitle)
                .containsExactly("Effective Java", "Java Puzzlers");
    }

    @Test
    void recordProjectionWithAConstructorExpression() {
        assertThat(books.findCardsCheaperThan(new BigDecimal("86")))
                .containsExactly(new BookCard("Java Puzzlers", "Joshua Bloch", new BigDecimal("55.00")),
                        new BookCard("Refactoring", "Martin Fowler", new BigDecimal("85.00")));
    }
}
