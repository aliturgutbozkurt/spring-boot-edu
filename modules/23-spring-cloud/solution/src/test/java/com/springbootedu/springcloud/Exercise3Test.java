package com.springbootedu.springcloud;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext                                               // the test changes the environment
class Exercise3Test {

    @DynamicPropertySource
    static void backends(DynamicPropertyRegistry registry) {
        Backends.register(registry);
    }

    @Autowired
    WebTestClient http;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    ContextRefresher refresher;

    @BeforeEach
    void healthyReviews() {
        Backends.reviewsHealthy();
    }

    @Test
    void aNewKeyWorksAfterARefreshWithoutARestart() {
        http.get().uri("/api/books/9780134685991").header("X-Api-Key", "dev-key").exchange().expectStatus().isOk();

        environment.getPropertySources().addFirst(
                new MapPropertySource("rotated", Map.of("bookstore.gateway.api-key", "rotated-key")));
        refresher.refresh();                                  // what POST /actuator/refresh does

        http.get().uri("/api/books/9780134685991").header("X-Api-Key", "rotated-key").exchange().expectStatus().isOk();
        http.get().uri("/api/books/9780134685991").header("X-Api-Key", "dev-key").exchange().expectStatus().isUnauthorized();
    }
}
