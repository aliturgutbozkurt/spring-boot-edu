package com.springbootedu.dockerdeployment;

import java.io.IOException;
import java.net.ServerSocket;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Exercise 3 — the warehouse is part of readiness, but not of liveness.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Import(TestcontainersConfiguration.class)
class Exercise3IT {

    static final ServerSocket WAREHOUSE = openWarehouse();

    @DynamicPropertySource
    static void warehouse(DynamicPropertyRegistry registry) {
        registry.add("bookstore.warehouse.host", () -> "localhost");
        registry.add("bookstore.warehouse.port", WAREHOUSE::getLocalPort);
    }

    @Autowired
    RestTestClient http;

    @AfterAll
    static void closeWarehouse() throws IOException {
        WAREHOUSE.close();
    }

    @Test
    void withoutTheWarehouseTheAppIsAliveButNotReady() throws IOException {
        http.get().uri("/actuator/health/readiness").exchange().expectStatus().isOk();

        WAREHOUSE.close();                                        // the warehouse goes away

        http.get().uri("/actuator/health/readiness").exchange()
                .expectStatus().isEqualTo(503)
                .expectBody().jsonPath("$.status").isEqualTo("DOWN");
        http.get().uri("/actuator/health/liveness").exchange().expectStatus().isOk();
    }

    private static ServerSocket openWarehouse() {
        try {
            return new ServerSocket(0);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
