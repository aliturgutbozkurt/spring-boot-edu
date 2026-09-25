package com.springbootedu.capstone.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * C.6 — the whole platform, from the outside: the real images of capstone/compose.yaml, only through the gateway.
 * One order touches PostgreSQL (order + outbox), MongoDB (stock), Hazelcast (lock), Kafka (events),
 * Elasticsearch (read model) and Redis (search cache, rate limit) — SPEC success criterion 6.
 */
class PlatformIT {

    static final String KAFKA_BOOK = "9781492078005";            // seeded by the catalog with stock 5

    // tag::platform[]
    static final ComposeContainer PLATFORM = new ComposeContainer(
            new File("../compose.yaml"), new File("compose.e2e.yaml"))
            .withBuild(true)                                     // images from the jars of this build
            .withServices("gateway")                             // and everything it depends on (not LGTM)
            .withExposedService("gateway", 8080, Wait.forHttp("/actuator/health/readiness")
                    .forStatusCode(200).withStartupTimeout(Duration.ofMinutes(10)));
    // end::platform[]

    static final HttpClient HTTP = HttpClient.newHttpClient();
    static final JsonMapper JSON = JsonMapper.builder().build();

    @BeforeAll
    static void start() {
        PLATFORM.start();
    }

    @AfterAll
    static void stop() {
        PLATFORM.stop();
    }

    @Test
    void anOrderFlowsFromTheGatewayToTheSearchIndex() throws Exception {
        // the seed data reached the search index through Kafka (catalog → BookChanged → search)
        await().atMost(Duration.ofMinutes(2)).ignoreExceptions().untilAsserted(() ->
                assertThat(searchHit("definitive")).isNotNull());
        long soldBefore = searchHit("definitive").get("sold").asLong();   // now also cached in Redis
        int stockBefore = book(KAFKA_BOOK).get("stock").asInt();

        String token = token("ayse", "ayse-demo");
        HttpResponse<String> placed = post("/api/orders", token,
                "{\"lines\":[{\"isbn\":\"" + KAFKA_BOOK + "\",\"quantity\":2}]}");

        assertThat(placed.statusCode()).isEqualTo(201);                    // stock reserved over gRPC
        assertThat(JSON.readTree(placed.body()).get("total").decimalValue()).isEqualByComparingTo("198.00");
        assertThat(book(KAFKA_BOOK).get("stock").asInt()).isEqualTo(stockBefore - 2);

        // outbox → Kafka → search index; the cached result was evicted, so the new number is visible
        await().atMost(Duration.ofSeconds(60)).ignoreExceptions().untilAsserted(() ->
                assertThat(searchHit("definitive").get("sold").asLong()).isEqualTo(soldBefore + 2));
    }

    @Test
    void theGatewayProtectsTheServices() throws Exception {
        assertThat(post("/api/orders", null, "{\"lines\":[]}").statusCode()).isEqualTo(401);

        String token = token("ayse", "ayse-demo");
        HttpResponse<String> tooMany = post("/api/orders", token,
                "{\"lines\":[{\"isbn\":\"" + KAFKA_BOOK + "\",\"quantity\":99}]}");
        assertThat(tooMany.statusCode()).isEqualTo(409);                   // ProblemDetail from the order service
        assertThat(tooMany.body()).contains("Not enough stock");
    }

    private static JsonNode searchHit(String query) throws Exception {
        JsonNode hits = JSON.readTree(get("/api/search?q=" + query).body()).get("hits");
        for (JsonNode hit : hits) {
            if (KAFKA_BOOK.equals(hit.get("isbn").asString())) {
                return hit;
            }
        }
        return null;
    }

    private static JsonNode book(String isbn) throws Exception {
        return JSON.readTree(get("/api/catalog/books/" + isbn).body());
    }

    private static String token(String username, String password) throws Exception {
        HttpResponse<String> response = post("/api/auth/token", null,
                "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}");
        assertThat(response.statusCode()).isEqualTo(200);
        return JSON.readTree(response.body()).get("accessToken").asString();
    }

    private static HttpResponse<String> get(String path) throws Exception {
        return HTTP.send(HttpRequest.newBuilder(uri(path)).build(), HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> post(String path, String token, String json) throws Exception {
        var request = HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        return HTTP.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static URI uri(String path) {
        return URI.create("http://" + PLATFORM.getServiceHost("gateway", 8080) + ":"
                + PLATFORM.getServicePort("gateway", 8080) + path);
    }
}
