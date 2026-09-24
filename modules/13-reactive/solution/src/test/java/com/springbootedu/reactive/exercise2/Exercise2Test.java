package com.springbootedu.reactive.exercise2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Exercise 2 — a live stock feed with Server-Sent Events.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class Exercise2Test {

    @Autowired
    WebTestClient http;

    @Test
    void everyStockChangeIsPushedToConnectedClients() {
        Flux<StockLevel> feed = connect("/api/stock/stream");

        StepVerifier.create(feed.take(2))
                .then(() -> update("9780134685991", 7))
                .then(() -> update("9781617297571", 3))
                .expectNext(new StockLevel("9780134685991", 7))
                .expectNext(new StockLevel("9781617297571", 3))
                .verifyComplete();
    }

    @Test
    void aClientCanFollowASingleBook() {
        Flux<StockLevel> feed = connect("/api/stock/stream?isbn=9781617297571");

        StepVerifier.create(feed.take(1))
                .then(() -> update("9780134685991", 1))              // another book: filtered out
                .then(() -> update("9781617297571", 9))
                .expectNext(new StockLevel("9781617297571", 9))
                .verifyComplete();
    }

    @Test
    void theStreamIsAnEventStream() {
        http.get().uri("/api/stock/stream").accept(MediaType.TEXT_EVENT_STREAM).exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM);   // before any stock change
    }

    private Flux<StockLevel> connect(String uri) {
        return http.get().uri(uri).accept(MediaType.TEXT_EVENT_STREAM).exchange()
                .expectStatus().isOk()
                .returnResult(StockLevel.class).getResponseBody();
    }

    private void update(String isbn, int quantity) {
        http.put().uri("/api/stock/{isbn}", isbn).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(quantity).exchange().expectStatus().isNoContent();
    }
}
