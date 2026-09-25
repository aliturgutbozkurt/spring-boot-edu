package com.springbootedu.capstone.gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.redis.testcontainers.RedisContainer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.utility.DockerImageName;

/**
 * C.5 — the gateway routes to the three services, checks JWTs, issues dev tokens and limits the rate.
 * One WireMock stands in for all services (one server per JVM, see CLAUDE.md).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "bookstore.jwt.secret=" + Tokens.SECRET,
        "bookstore.rate-limit.replenish-rate=2",
        "bookstore.rate-limit.burst-capacity=4",
        "bookstore.dev-tokens.enabled=true",
        "bookstore.dev-tokens.users[0].username=ayse",
        "bookstore.dev-tokens.users[0].password=test-password",
        "bookstore.dev-tokens.users[0].roles=USER"})
@AutoConfigureWebTestClient
@Import(GatewayTest.Redis.class)
class GatewayTest {

    static final WireMockServer SERVICES = new WireMockServer(options().dynamicPort());

    static {
        SERVICES.start();
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class Redis {

        static final RedisContainer REDIS = new RedisContainer(DockerImageName.parse("redis:8.8.3-alpine"));

        @Bean
        @ServiceConnection
        RedisContainer redisContainer() {
            return REDIS;
        }
    }

    @DynamicPropertySource
    static void services(DynamicPropertyRegistry registry) {
        registry.add("bookstore.services.orders", SERVICES::baseUrl);
        registry.add("bookstore.services.catalog", SERVICES::baseUrl);
        registry.add("bookstore.services.search", SERVICES::baseUrl);
    }

    @Autowired
    WebTestClient http;

    String customer = "customer-" + UUID.randomUUID();

    @BeforeEach
    void everyServiceAnswers() {
        SERVICES.resetAll();
        SERVICES.stubFor(any(urlPathMatching("/api/.*")).willReturn(aResponse()
                .withHeader("Content-Type", "application/json").withBody("{\"ok\": true}")));
    }

    @Test
    void searchAndCatalogReadsArePublic() {
        http.get().uri("/api/search?q=java").exchange().expectStatus().isOk();
        http.get().uri("/api/catalog/books").exchange().expectStatus().isOk();

        SERVICES.verify(getRequestedFor(urlPathEqualTo("/api/search")));
        SERVICES.verify(getRequestedFor(urlPathEqualTo("/api/catalog/books")));
    }

    @Test
    void ordersNeedAToken() {
        http.post().uri("/api/orders").contentType(MediaType.APPLICATION_JSON).bodyValue("{}").exchange()
                .expectStatus().isUnauthorized();

        assertThat(SERVICES.getAllServeEvents()).isEmpty();              // stopped at the gateway
    }

    @Test
    void theTokenIsForwardedToTheService() {
        String token = Tokens.bearer(customer, "USER");

        http.post().uri("/api/orders").header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON).bodyValue("{}").exchange()
                .expectStatus().isOk();

        SERVICES.verify(postRequestedFor(urlPathEqualTo("/api/orders"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo(token)));        // defence in depth (ADR-5)
    }

    @Test
    void onlyAnAdminMayChangeTheCatalog() {
        http.put().uri("/api/catalog/books/9780134685991").header(HttpHeaders.AUTHORIZATION, Tokens.bearer(customer, "USER"))
                .contentType(MediaType.APPLICATION_JSON).bodyValue("{}").exchange()
                .expectStatus().isForbidden();
        http.put().uri("/api/catalog/books/9780134685991").header(HttpHeaders.AUTHORIZATION, Tokens.bearer("admin", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON).bodyValue("{}").exchange()
                .expectStatus().isOk();
    }

    @Test
    void aDemoUserGetsAWorkingToken() {
        String token = http.post().uri("/api/auth/token").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("username", "ayse", "password", "test-password")).exchange()
                .expectStatus().isOk()
                .expectBody(TokenResponse.class).returnResult().getResponseBody().accessToken();

        http.get().uri("/api/orders").header(HttpHeaders.AUTHORIZATION, "Bearer " + token).exchange()
                .expectStatus().isOk();
    }

    @Test
    void aWrongPasswordGetsNoToken() {
        http.post().uri("/api/auth/token").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("username", "ayse", "password", "guess")).exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void aCustomerSendingTooManyOrdersIsSlowedDown() {
        String token = Tokens.bearer(customer, "USER");
        List<Integer> statuses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {                                   // burst 4, 2 new tokens per second
            statuses.add(http.post().uri("/api/orders").header(HttpHeaders.AUTHORIZATION, token)
                    .contentType(MediaType.APPLICATION_JSON).bodyValue("{}").exchange()
                    .returnResult(String.class).getStatus().value());
        }

        assertThat(statuses).contains(200, 429);                          // 429 Too Many Requests
        String otherCustomer = Tokens.bearer("other-" + UUID.randomUUID(), "USER");
        http.post().uri("/api/orders").header(HttpHeaders.AUTHORIZATION, otherCustomer)
                .contentType(MediaType.APPLICATION_JSON).bodyValue("{}").exchange()
                .expectStatus().isOk();                                   // each customer has its own bucket
    }

    record TokenResponse(String accessToken, String tokenType, long expiresIn) {
    }
}
