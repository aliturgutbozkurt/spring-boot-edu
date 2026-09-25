package com.springbootedu.springcloud.gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.redis.testcontainers.RedisContainer;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.utility.DockerImageName;

/**
 * Lesson 3.1 — the gateway routes by path to the services, adds a header, and limits the request rate.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(GatewayTest.Redis.class)
class GatewayTest {

    static final WireMockServer CATALOG = new WireMockServer(options().dynamicPort());
    static final WireMockServer ORDERS = new WireMockServer(options().dynamicPort());

    static {
        CATALOG.start();
        ORDERS.start();
        CATALOG.stubFor(get(urlPathMatching("/api/books/.*")).willReturn(aResponse()
                .withHeader("Content-Type", "application/json").withBody("{\"title\": \"Effective Java\"}")));
        ORDERS.stubFor(post(urlPathMatching("/api/orders.*")).willReturn(aResponse()
                .withHeader("Content-Type", "application/json").withBody("{\"status\": \"PRICED\"}")));
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class Redis {

        @Bean
        @ServiceConnection
        RedisContainer redisContainer() {
            return new RedisContainer(DockerImageName.parse("redis:8.8.3-alpine"));
        }
    }

    @DynamicPropertySource
    static void services(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.discovery.client.simple.instances.catalog-service[0].uri", CATALOG::baseUrl);
        registry.add("spring.cloud.discovery.client.simple.instances.catalog-service[1].uri", CATALOG::baseUrl);
        registry.add("spring.cloud.discovery.client.simple.instances.order-service[0].uri", ORDERS::baseUrl);
    }

    @Autowired
    WebTestClient http;

    @Test
    void thePathDecidesTheService() {
        http.get().uri("/api/books/9780134685991").exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Served-Through", "bookstore-gateway")
                .expectBody().jsonPath("$.title").isEqualTo("Effective Java");

        http.post().uri("/api/orders").bodyValue("{}").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.status").isEqualTo("PRICED");
    }

    @Test
    void anUnknownPathHasNoRoute() {
        http.get().uri("/api/unknown").exchange().expectStatus().isNotFound();
    }

    // tag::rate-limit-test[]
    @Test
    void tooManyRequestsAreRejected() {
        List<Integer> statuses = new ArrayList<>();
        for (int i = 0; i < 20; i++) {                      // burst capacity 10, 5 new tokens per second
            statuses.add(http.get().uri("/api/books/9780134685991").header("X-Customer", "rate-limit-test").exchange()
                    .returnResult(String.class).getStatus().value());
        }

        assertThat(statuses).contains(200, 429);             // 429 Too Many Requests
    }
    // end::rate-limit-test[]
}
