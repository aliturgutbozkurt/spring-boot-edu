package com.springbootedu.reactive.book;

import com.springbootedu.reactive.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Lessons 3.4 and 3.6 — functional endpoints on top of R2DBC: no thread waits for PostgreSQL.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@AutoConfigureWebTestClient
@Import(TestcontainersConfiguration.class)
class BookApiTest {

    @Autowired
    WebTestClient http;

    @Test
    void listsTheBooksFromPostgres() {
        http.get().uri("/api/books").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$[?(@.isbn == '9780134685991')].title").isEqualTo("Effective Java");
    }

    @Test
    void findsOneBookOrAnswers404() {
        http.get().uri("/api/books/9781617297571").exchange()
                .expectStatus().isOk().expectBody().jsonPath("$.title").isEqualTo("Spring in Action");
        http.get().uri("/api/books/0000000000000").exchange().expectStatus().isNotFound();
    }

    @Test
    void createsABook() {
        http.post().uri("/api/books").contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"isbn": "9780000000017", "title": "Reactive Spring", "price": 42.00}""")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", ".*/api/books/9780000000017")
                .expectBody().jsonPath("$.id").isNumber();
    }
}
