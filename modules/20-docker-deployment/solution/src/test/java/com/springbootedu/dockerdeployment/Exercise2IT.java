package com.springbootedu.dockerdeployment;

import static org.assertj.core.api.Assertions.assertThat;

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

/**
 * Exercise 2 — starts exercise/compose.yaml and waits until the app is ready (database and warehouse reachable).
 */
class Exercise2IT {

    static final ComposeContainer SYSTEM = new ComposeContainer(new File("compose.yaml"))
            .withBuild(true)
            .withExposedService("app", 8080,
                    Wait.forHttp("/actuator/health/readiness").forStatusCode(200)
                            .withStartupTimeout(Duration.ofMinutes(3)));

    @BeforeAll
    static void start() {
        SYSTEM.start();
    }

    @AfterAll
    static void stop() {
        SYSTEM.stop();
    }

    @Test
    void theAppIsReadyWithItsDatabaseAndTheWarehouse() throws Exception {
        String readiness = get("/actuator/health/readiness");

        assertThat(readiness).contains("\"status\":\"UP\"");
    }

    @Test
    void theAppServesBooksFromItsDatabase() throws Exception {
        assertThat(get("/api/books")).contains("Effective Java");
    }

    private static String get(String path) throws Exception {
        String url = "http://" + SYSTEM.getServiceHost("app", 8080) + ":" + SYSTEM.getServicePort("app", 8080) + path;
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(HttpRequest.newBuilder(URI.create(url)).build(), HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        return response.body();
    }
}
