package com.springbootedu.datajpapostgres.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.datajpapostgres.TestcontainersConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

/**
 * Lesson 3.3 — the N+1 problem, measured with Hibernate statistics, and two ways to fix it.
 */
@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@Import(TestcontainersConfiguration.class)
class NPlusOneTest {

    @Autowired
    AuthorRepository authors;

    @Autowired
    EntityManager entityManager;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    Statistics statistics;

    @BeforeEach
    void startCounting() {
        entityManager.clear();                                        // nothing cached from earlier work
        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();
    }

    private static int countBooks(List<Author> list) {
        return list.stream().mapToInt(author -> author.getBooks().size()).sum();   // touches every lazy collection
    }

    // tag::measure[]
    @Test
    void lazyCollectionsCauseOneQueryPerAuthor() {
        List<Author> all = authors.findAll();
        int books = countBooks(all);

        assertThat(books).isEqualTo(6);
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1 + all.size()); // 1 for authors + 1 per author
    }
    // end::measure[]

    @Test
    void anEntityGraphLoadsEverythingInOneQuery() {
        int books = countBooks(authors.findAllWithBooksBy());

        assertThat(books).isEqualTo(6);
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    void aJoinFetchQueryDoesTheSame() {
        int books = countBooks(authors.findAllFetchingBooks());

        assertThat(books).isEqualTo(6);
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
    }
}
