package com.springbootedu.observability.lgtm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.springbootedu.observability.order.OrderResult;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.client.RestClient;
import org.testcontainers.grafana.LgtmStackContainer;

/**
 * Lesson 3.7 — the real thing: metrics and traces are pushed over OTLP into Grafana LGTM (Mimir/Prometheus, Tempo).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.test.metrics.export=true",                    // tests switch export off by default
        "spring.test.tracing.export=true",
        "management.otlp.metrics.export.step=1s"})
@AutoConfigureRestTestClient
@Import(GrafanaLgtmIT.Lgtm.class)
class GrafanaLgtmIT {

    // tag::testcontainers[]
    @TestConfiguration(proxyBeanMethods = false)
    static class Lgtm {

        static final LgtmStackContainer LGTM = new LgtmStackContainer("grafana/otel-lgtm:0.33.1");   // as compose.yaml

        @Bean
        @ServiceConnection                                    // OTLP endpoints for metrics, traces and logs
        LgtmStackContainer lgtm() {
            return LGTM;
        }
    }
    // end::testcontainers[]

    @Autowired
    RestTestClient http;

    @Test
    void metricsAndTracesArriveInGrafana() {
        OrderResult result = http.post().uri("/api/orders?isbn=9780134685991&channel=web").exchange()
                .expectStatus().isOk()
                .expectBody(OrderResult.class).returnResult().getResponseBody();
        assertThat(result).isNotNull();

        RestClient prometheus = RestClient.create(Lgtm.LGTM.getPrometheusHttpUrl());
        await().atMost(Duration.ofSeconds(60)).pollInterval(Duration.ofSeconds(2)).ignoreExceptions().untilAsserted(() ->
                assertThat(prometheus.get().uri("/api/v1/query?query={q}", "bookstore_orders_placed_total")
                        .retrieve().body(String.class)).contains("\"channel\":\"web\""));

        RestClient tempo = RestClient.create(Lgtm.LGTM.getTempoUrl());
        // Tempo answers 404 until the trace is stored: ignoreExceptions() keeps polling instead of failing at once
        await().atMost(Duration.ofSeconds(60)).pollInterval(Duration.ofSeconds(2)).ignoreExceptions().untilAsserted(() ->
                assertThat(tempo.get().uri("/api/traces/{id}", result.traceId())
                        .retrieve().body(String.class)).contains("calculate-price"));   // the @Observed span
    }
}
