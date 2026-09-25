package com.springbootedu.dockerdeployment;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Lesson 3.4 — SIGTERM (docker stop, Kubernetes) closes the context: running requests still finish.
 */
class GracefulShutdownTest {

    @Test
    void aRunningRequestFinishesDuringShutdown() throws Exception {
        TestcontainersConfiguration.POSTGRES.start();
        ConfigurableApplicationContext app = new SpringApplicationBuilder(DockerDeploymentApplication.class)
                .properties("server.port=0", "bookstore.tour.enabled=false", "spring.docker.compose.enabled=false",
                        "spring.datasource.url=" + TestcontainersConfiguration.POSTGRES.getJdbcUrl(),
                        "spring.datasource.username=" + TestcontainersConfiguration.POSTGRES.getUsername(),
                        "spring.datasource.password=" + TestcontainersConfiguration.POSTGRES.getPassword())
                .run();
        int port = ((WebServerApplicationContext) app).getWebServer().getPort();

        CompletableFuture<HttpResponse<String>> slowRequest = HttpClient.newHttpClient().sendAsync(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/slow?millis=1500")).build(),
                HttpResponse.BodyHandlers.ofString());
        Thread.sleep(500);                                    // the request is running now

        app.close();                                          // what SIGTERM triggers; waits for running requests

        HttpResponse<String> response = slowRequest.join();
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("done after 1500 ms");
    }
}
