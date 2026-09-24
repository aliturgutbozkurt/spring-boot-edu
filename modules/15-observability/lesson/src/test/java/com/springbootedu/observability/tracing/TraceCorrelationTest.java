package com.springbootedu.observability.tracing;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.observability.order.OrderResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Lesson 3.5 — one trace across an HTTP call, and JSON log lines that carry the trace id.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@AutoConfigureTracing                                // real tracing in the test (export stays off)
@ExtendWith(OutputCaptureExtension.class)
class TraceCorrelationTest {

    @Autowired
    RestTestClient http;

    @Autowired
    JsonMapper json;

    @Test
    void theTraceFollowsTheCallAndAppearsInTheLogs(CapturedOutput output) {
        OrderResult result = http.post().uri("/api/orders?isbn=9780134685991").exchange()
                .expectStatus().isOk()
                .expectBody(OrderResult.class).returnResult().getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.traceId()).hasSize(32);                         // W3C trace id: 16 bytes, hex

        List<JsonNode> lines = output.getOut().lines()
                .filter(line -> line.startsWith("{"))
                .map(json::readTree)
                .filter(line -> line.path("traceId").asString("").equals(result.traceId()))   // from the MDC
                .toList();
        assertThat(lines).extracting(line -> line.path("message").asString())
                .anyMatch(message -> message.startsWith("order placed"))     // the order service …
                .anyMatch(message -> message.startsWith("catalog lookup"));  // … and the called "service"
    }
}
