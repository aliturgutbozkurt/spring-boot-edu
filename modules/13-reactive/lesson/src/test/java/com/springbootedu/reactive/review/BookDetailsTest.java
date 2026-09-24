package com.springbootedu.reactive.review;

import com.springbootedu.reactive.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Lesson 3.5 — one response from two databases: the book from PostgreSQL, its reviews from MongoDB, fetched together.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@AutoConfigureWebTestClient
@Import(TestcontainersConfiguration.class)
class BookDetailsTest {

    @Autowired
    WebTestClient http;

    @Autowired
    ReviewRepository reviews;

    @BeforeEach
    void seed() {
        reviews.deleteAll()
                .then(reviews.save(new Review(null, "9780321336781", "ada", 5, "Fun and scary.")))
                .block();                                         // fine in a test: set up, then assert
    }

    @Test
    void combinesTheBookAndItsReviews() {
        http.get().uri("/api/books/9780321336781/details").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Java Puzzlers")
                .jsonPath("$.averageStars").isEqualTo(5.0)
                .jsonPath("$.reviews[0].text").isEqualTo("Fun and scary.");
    }

    @Test
    void aBookWithoutReviewsStillHasDetails() {
        http.get().uri("/api/books/9781617297571/details").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.reviews").isEmpty();
    }

    @Test
    void anUnknownBookIs404() {
        http.get().uri("/api/books/0000000000000/details").exchange().expectStatus().isNotFound();
    }

    @Test
    void reviewsCanBeAddedThroughTheAnnotatedController() {
        http.post().uri("/api/books/9781617297571/reviews")
                .bodyValue(new NewReview("bob", 4, "Clear and practical."))
                .exchange()
                .expectStatus().isCreated();

        http.get().uri("/api/books/9781617297571/reviews").exchange()
                .expectStatus().isOk().expectBody().jsonPath("$[0].author").isEqualTo("bob");
    }
}
