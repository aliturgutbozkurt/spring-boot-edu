package com.springbootedu.grpc;

import static org.assertj.core.api.Assertions.assertThat;

import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.grpc.client.ImportGrpcClients;

/**
 * Lesson 3.7 — the standard gRPC health service (grpc.health.v1), fed by Boot's health indicators.
 */
@SpringBootTest(properties = {"bookstore.tour.enabled=false", "spring.grpc.server.health.schedule.delay=0s"})
@AutoConfigureTestGrpcTransport
@ImportGrpcClients(types = HealthGrpc.HealthBlockingStub.class)
class HealthServiceTest {

    @Autowired
    HealthGrpc.HealthBlockingStub health;

    @Test
    void theServerIsServing() {
        HealthCheckResponse response = health.check(HealthCheckRequest.newBuilder().setService("").build());

        assertThat(response.getStatus()).isEqualTo(HealthCheckResponse.ServingStatus.SERVING);
    }
}
