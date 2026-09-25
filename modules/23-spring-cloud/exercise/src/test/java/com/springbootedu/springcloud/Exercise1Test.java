package com.springbootedu.springcloud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class Exercise1Test {

    @DynamicPropertySource
    static void backends(DynamicPropertyRegistry registry) {
        Backends.register(registry);
    }

    @Autowired
    WebTestClient http;

    @BeforeEach
    void healthyReviews() {
        Backends.reviewsHealthy();
    }

    @Test
    void theNewRouteReachesTheReviewService() {
        http.get().uri("/api/reviews/9780134685991").header("X-Api-Key", "dev-key").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.reviews[0].stars").isEqualTo(5);
    }

    @Test
    void withoutAKeyTheGatewayAnswers401() {
        http.get().uri("/api/reviews/9780134685991").exchange().expectStatus().isUnauthorized();
        http.get().uri("/api/books/9780134685991").exchange().expectStatus().isUnauthorized();
    }

    @Test
    void aWrongKeyIsRejectedAndTheCatalogStillWorks() {
        http.get().uri("/api/books/9780134685991").header("X-Api-Key", "wrong").exchange()
                .expectStatus().isUnauthorized();
        http.get().uri("/api/books/9780134685991").header("X-Api-Key", "dev-key").exchange()
                .expectStatus().isOk();
    }
}
