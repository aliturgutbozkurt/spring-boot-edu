package com.springbootedu.dockerdeployment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Lesson 3.3 — liveness says "restart me if I am broken", readiness says "send me traffic".
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "bookstore.tour.enabled=false")
@AutoConfigureRestTestClient
@Import(TestcontainersConfiguration.class)
class HealthProbesTest {

    @Autowired
    RestTestClient http;

    @Autowired
    ApplicationContext context;

    @Test
    void bothProbesAreUp() {
        http.get().uri("/actuator/health/liveness").exchange()
                .expectStatus().isOk().expectBody().jsonPath("$.status").isEqualTo("UP");
        http.get().uri("/actuator/health/readiness").exchange()
                .expectStatus().isOk().expectBody().jsonPath("$.status").isEqualTo("UP");
    }

    // tag::refusing-traffic[]
    @Test
    void anApplicationThatRefusesTrafficIsAliveButNotReady() {
        AvailabilityChangeEvent.publish(context, ReadinessState.REFUSING_TRAFFIC);   // e.g. during a long cache warm-up
        try {
            http.get().uri("/actuator/health/readiness").exchange()
                    .expectStatus().isEqualTo(503)
                    .expectBody().jsonPath("$.status").isEqualTo("OUT_OF_SERVICE");
            http.get().uri("/actuator/health/liveness").exchange().expectStatus().isOk();
        } finally {
            AvailabilityChangeEvent.publish(context, ReadinessState.ACCEPTING_TRAFFIC);
        }
    }
    // end::refusing-traffic[]

    @Test
    void theApiWorks() {
        http.get().uri("/api/books").exchange()
                .expectStatus().isOk().expectBody().jsonPath("$[0].title").isEqualTo("Designing Data-Intensive Applications");
    }
}
