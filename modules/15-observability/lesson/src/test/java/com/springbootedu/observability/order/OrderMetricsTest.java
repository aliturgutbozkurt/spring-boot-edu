package com.springbootedu.observability.order;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Lesson 3.2 — business metrics: a counter with a tag, a timer and a gauge.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class OrderMetricsTest {

    @Autowired
    RestTestClient http;

    @Autowired
    MeterRegistry registry;

    @Test
    void everyOrderIsCountedPerChannelAndTimed() {
        double webBefore = count("web");
        long timedBefore = registry.get("bookstore.order.processing").timer().count();

        order("web");
        order("web");
        order("app");

        assertThat(count("web") - webBefore).isEqualTo(2);
        assertThat(registry.get("bookstore.order.processing").timer().count() - timedBefore).isEqualTo(3);
        assertThat(registry.get("bookstore.orders.in.progress").gauge().value()).isZero();   // nothing running now
    }

    @Test
    void theMetricsEndpointShowsTheCounterAndItsTags() {
        order("app");

        http.get().uri("/actuator/metrics/bookstore.orders.placed?tag=channel:app").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.measurements[0].statistic").isEqualTo("COUNT")
                .jsonPath("$.availableTags[?(@.tag == 'application')].values[0]").isEqualTo("15-observability");
    }

    private void order(String channel) {
        http.post().uri("/api/orders?isbn=9780134685991&channel={channel}", channel).exchange().expectStatus().isOk();
    }

    private double count(String channel) {
        var counter = registry.find("bookstore.orders.placed").tag("channel", channel).counter();
        return counter == null ? 0 : counter.count();
    }
}
