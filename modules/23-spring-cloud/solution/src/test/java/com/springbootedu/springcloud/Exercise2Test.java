package com.springbootedu.springcloud;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
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
class Exercise2Test {

    @DynamicPropertySource
    static void backends(DynamicPropertyRegistry registry) {
        Backends.register(registry);
    }

    @Autowired
    WebTestClient http;

    @Autowired
    CircuitBreakerRegistry breakers;

    @BeforeEach
    void closedBreaker() {
        breakers.circuitBreaker("reviews").reset();
    }

    @Test
    void aFailingServiceGivesTheFallback() {
        Backends.reviewsFailing();

        http.get().uri("/api/reviews/9780134685991").header("X-Api-Key", "dev-key").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.fallback").isEqualTo(true);
    }

    @Test
    void repeatedFailuresOpenTheBreaker() {
        Backends.reviewsFailing();

        for (int i = 0; i < 4; i++) {
            http.get().uri("/api/reviews/9780134685991").header("X-Api-Key", "dev-key").exchange();
        }

        assertThat(breakers.circuitBreaker("reviews").getState()).isEqualTo(CircuitBreaker.State.OPEN);
        int callsBefore = Backends.REVIEWS.getAllServeEvents().size();
        http.get().uri("/api/reviews/9780134685991").header("X-Api-Key", "dev-key").exchange()
                .expectBody().jsonPath("$.fallback").isEqualTo(true);
        assertThat(Backends.REVIEWS.getAllServeEvents()).hasSize(callsBefore);   // open: not called at all
    }
}
