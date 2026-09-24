package com.springbootedu.observability.exercise3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Exercise 3 — the indicator is picked up by Actuator automatically.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class Exercise3ApplicationTest {

    @Autowired
    RestTestClient http;

    @Test
    void theIndicatorAppearsInTheHealthEndpoint() {
        http.get().uri("/actuator/health").exchange()
                .expectBody().jsonPath("$.components.paymentProvider.status").isEqualTo("UP");
    }
}
