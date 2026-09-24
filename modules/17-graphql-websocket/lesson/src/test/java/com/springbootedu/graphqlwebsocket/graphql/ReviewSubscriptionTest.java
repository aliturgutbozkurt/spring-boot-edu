package com.springbootedu.graphqlwebsocket.graphql;

import com.springbootedu.graphqlwebsocket.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Lesson 3.5 — a subscription is a stream of results: every new review of the book is pushed.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@AutoConfigureGraphQlTester
@Import(TestcontainersConfiguration.class)
class ReviewSubscriptionTest {

    @Autowired
    GraphQlTester graphQl;

    @Autowired
    ReviewService reviews;

    @Test
    void newReviewsOfTheBookArePushed() {
        Flux<String> texts = graphQl.document("subscription { reviewAdded(isbn: \"9781449373320\") { text } }")
                .executeSubscription()
                .toFlux("reviewAdded.text", String.class);

        StepVerifier.create(texts.take(2))
                .then(() -> reviews.add(new ReviewInput("9781449373320", 5, "Eye-opening.")))
                .then(() -> reviews.add(new ReviewInput("9780134685991", 4, "Another book: filtered out.")))
                .then(() -> reviews.add(new ReviewInput("9781449373320", 4, "Long but great.")))
                .expectNext("Eye-opening.", "Long but great.")
                .verifyComplete();
    }
}
