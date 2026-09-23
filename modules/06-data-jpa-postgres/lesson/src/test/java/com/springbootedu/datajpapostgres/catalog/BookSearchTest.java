package com.springbootedu.datajpapostgres.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.datajpapostgres.TestcontainersConfiguration;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Lesson 3.5 — dynamic search: only the filled-in criteria become part of the query.
 */
@DataJpaTest
@Import({TestcontainersConfiguration.class, BookSearch.class})
class BookSearchTest {

    @Autowired
    BookSearch search;

    private static final PageRequest FIRST_PAGE = PageRequest.of(0, 10, Sort.by("title"));

    @Test
    void noCriteriaReturnsEverything() {
        assertThat(search.search(new BookFilter(null, null, null), FIRST_PAGE).getTotalElements()).isEqualTo(6);
    }

    @Test
    void combinesTitlePriceAndCategory() {
        assertThat(search.search(new BookFilter("java", new BigDecimal("100"), "java"), FIRST_PAGE).getContent())
                .extracting(BookCard::title)
                .containsExactly("Effective Java", "Java Puzzlers");
    }

    @Test
    void categoryOnly() {
        assertThat(search.search(new BookFilter(null, null, "spring"), FIRST_PAGE).getContent())
                .extracting(BookCard::title)
                .containsExactly("Spring Boot: Up and Running", "Spring in Action");
    }
}
