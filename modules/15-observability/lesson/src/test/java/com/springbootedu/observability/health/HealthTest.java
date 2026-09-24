package com.springbootedu.observability.health;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Lesson 3.1 — Actuator's health endpoint, a custom indicator, probes, and what is not exposed.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class HealthTest {

    @Autowired
    RestTestClient http;

    @Autowired
    Warehouse warehouse;

    @AfterEach
    void reset() {
        warehouse.setReachable(true);
    }

    @Test
    void theApplicationIsUpWithItsWarehouse() {
        http.get().uri("/actuator/health").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP")
                .jsonPath("$.components.warehouse.details.location").isEqualTo("Istanbul");
    }

    @Test
    void anUnreachableWarehouseMakesTheApplicationDown() {
        warehouse.setReachable(false);

        http.get().uri("/actuator/health").exchange()
                .expectStatus().isEqualTo(503)                          // load balancers take the instance out
                .expectBody().jsonPath("$.components.warehouse.status").isEqualTo("DOWN");
    }

    @Test
    void kubernetesProbesHaveTheirOwnGroups() {
        http.get().uri("/actuator/health/liveness").exchange().expectStatus().isOk();
        http.get().uri("/actuator/health/readiness").exchange().expectStatus().isOk();
    }

    @Test
    void sensitiveEndpointsAreNotExposed() {
        http.get().uri("/actuator/env").exchange().expectStatus().isNotFound();
        http.get().uri("/actuator/heapdump").exchange().expectStatus().isNotFound();
    }
}
