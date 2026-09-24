package com.springbootedu.reactive.review;

import com.springbootedu.reactive.TestcontainersConfiguration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

/**
 * Lesson 3.5 — a reactive MongoDB repository returns Mono and Flux.
 */
@DataMongoTest
@Import(TestcontainersConfiguration.class)
class ReviewRepositoryTest {

    @Autowired
    ReviewRepository reviews;

    @BeforeEach
    void seed() {
        StepVerifier.create(reviews.deleteAll().thenMany(reviews.saveAll(List.of(
                        new Review(null, "9780134685991", "ada", 5, "A classic."),
                        new Review(null, "9780134685991", "bob", 4, "Dense but worth it."),
                        new Review(null, "9781617297571", "ada", 3, "Good start.")))))
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findsTheReviewsOfABook() {
        StepVerifier.create(reviews.findByIsbnOrderByStarsDesc("9780134685991").map(Review::author))
                .expectNext("ada", "bob")
                .verifyComplete();
    }

    @Test
    void anUnknownBookHasNoReviews() {
        StepVerifier.create(reviews.findByIsbnOrderByStarsDesc("unknown")).verifyComplete();
    }
}
