package com.springbootedu.grpc.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Lesson 3.6 — the client side: an imported stub, and a deadline for every call.
 */
@SpringBootTest(properties = {"bookstore.tour.enabled=false", "bookstore.catalog.latency=300ms"})
@AutoConfigureTestGrpcTransport
class CatalogClientTest {

    @Autowired
    CatalogClient client;

    @Test
    void enoughTimeGivesTheTitle() {
        assertThat(client.titleOf("9780134685991", Duration.ofSeconds(2))).isEqualTo("Effective Java");
    }

    // tag::deadline-test[]
    @Test
    void aTooShortDeadlineEndsTheCall() {
        assertThatThrownBy(() -> client.titleOf("9780134685991", Duration.ofMillis(100)))   // server needs 300 ms
                .isInstanceOfSatisfying(StatusRuntimeException.class,
                        e -> assertThat(e.getStatus().getCode()).isEqualTo(Status.Code.DEADLINE_EXCEEDED));
    }
    // end::deadline-test[]
}
