package com.springbootedu.reactive.exercise1;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Exercise 1 — a reactive book API with functional endpoints.
 */
@SpringBootTest
@AutoConfigureWebTestClient
class Exercise1Test {

    @Autowired
    WebTestClient http;

    @Test
    void listsAllBooks() {
        http.get().uri("/api/books").exchange()
                .expectStatus().isOk()
                .expectBodyList(Book.class)
                .value(books -> assertThat(books).extracting(Book::isbn)
                        .contains("9780134685991"));                    // other tests may add or delete books
    }

    @Test
    void findsOneBookOrAnswers404() {
        http.get().uri("/api/books/9780134685991").exchange()
                .expectStatus().isOk().expectBody().jsonPath("$.title").isEqualTo("Effective Java");
        http.get().uri("/api/books/0000000000000").exchange().expectStatus().isNotFound();
    }

    @Test
    void createsABookWithALocation() {
        http.post().uri("/api/books").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new Book("9780000000017", "Reactive Spring"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/api/books/9780000000017");

        http.get().uri("/api/books/9780000000017").exchange().expectStatus().isOk();
    }

    @Test
    void deletesABookOrAnswers404() {
        http.delete().uri("/api/books/9781617297571").exchange().expectStatus().isNoContent();
        http.delete().uri("/api/books/9781617297571").exchange().expectStatus().isNotFound();
    }
}
