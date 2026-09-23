package com.springbootedu.elasticsearch.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import com.springbootedu.elasticsearch.TestcontainersConfiguration;
import com.springbootedu.elasticsearch.search.BookHit;
import com.springbootedu.elasticsearch.search.BookSearch;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Lessons 3.5–3.6 — PostgreSQL is the source of truth; committed changes reach the search index.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "bookstore.tour.enabled=false")
@AutoConfigureRestTestClient
@Import(TestcontainersConfiguration.class)
class BookIndexSyncIT {

    @Autowired
    BookStore store;

    @Autowired
    BookSearch search;

    @Autowired
    RestTestClient http;

    @Test
    void aBookSavedInPostgresBecomesSearchable() {
        store.add(new NewBook("9780000000017", "Kubernetes Patterns", "Bilgin Ibryam",
                "Bulut uygulamaları için tasarım kalıpları.", "architecture", new BigDecimal("80.00")));

        await().atMost(Duration.ofSeconds(10))
                .until(() -> search.search("kalıpları", null, null).stream()
                        .map(BookHit::title).toList().contains("Kubernetes Patterns"));
    }

    @Test
    void aRolledBackTransactionIsNeverIndexed() {
        NewBook existing = new NewBook("9780000000024", "Existing Book", "Somebody",
                "Katalogda zaten bulunan bir kitap.", "novel", new BigDecimal("10.00"));
        NewBook fresh = new NewBook("9780000000048", "Fresh Book", "Somebody",
                "Geri alınan işlemde eklenen kitap.", "novel", new BigDecimal("12.00"));
        store.add(existing);

        // "fresh" is inserted and its event published, then the duplicate fails and everything rolls back
        assertThatThrownBy(() -> store.addAll(List.of(fresh, existing))).isInstanceOf(DuplicateKeyException.class);

        await().during(Duration.ofSeconds(2)).atMost(Duration.ofSeconds(5))
                .until(() -> search.search("alınan", null, null).isEmpty());
    }

    @Test
    void theSearchApiReturnsHitsAndFacets() {
        store.add(new NewBook("9780000000031", "Domain-Driven Design", "Eric Evans",
                "Karmaşık yazılımların kalbindeki karmaşıklıkla başa çıkmak.", "architecture", new BigDecimal("99.00")));
        await().atMost(Duration.ofSeconds(10)).until(() -> !search.search("karmaşıklıkla", null, null).isEmpty());

        http.get().uri("/api/search?q=karmaşıklıkla").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.hits[0].title").isEqualTo("Domain-Driven Design")
                .jsonPath("$.categories.architecture").isEqualTo(1);
    }
}
