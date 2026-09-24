package com.springbootedu.graphqlwebsocket.graphql;

import java.time.Duration;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Lessons 3.4–3.5 — adds reviews and publishes every new one to the subscribers.
 */
@Service
public class ReviewService {

    private final CatalogRepository catalog;

    // tag::sink[]
    // A hot stream: every subscriber gets the reviews added after it subscribed (no history, no buffer).
    private final Sinks.Many<Review> added = Sinks.many().multicast().directBestEffort();
    // end::sink[]

    ReviewService(CatalogRepository catalog) {
        this.catalog = catalog;
    }

    // tag::add[]
    public Review add(ReviewInput input) {
        if (input.stars() < 1 || input.stars() > 5) {
            throw new InvalidReviewException("Stars must be between 1 and 5, not " + input.stars());
        }
        if (catalog.findBook(input.isbn()).isEmpty()) {
            throw new BookNotFoundException(input.isbn());
        }
        Review review = catalog.insertReview(input);
        // two requests may add reviews at the same time: retry briefly instead of failing on a concurrent emit
        added.emitNext(review, Sinks.EmitFailureHandler.busyLooping(Duration.ofMillis(100)));
        return review;
    }
    // end::add[]

    public Flux<Review> reviewsAdded() {
        return added.asFlux();
    }
}
