package com.springbootedu.dockerdeployment.warehouse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Exercise 3 — the health component "warehouse": UP when the warehouse accepts a TCP connection.
 */
@Component
@EnableConfigurationProperties(WarehouseProperties.class)
class WarehouseHealthIndicator implements HealthIndicator {

    private final WarehouseProperties warehouse;

    WarehouseHealthIndicator(WarehouseProperties warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public Health health() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(warehouse.host(), warehouse.port()), 500);
            return Health.up().withDetail("address", address()).build();
        } catch (IOException e) {
            return Health.down().withDetail("address", address()).withException(e).build();
        }
    }

    private String address() {
        return warehouse.host() + ":" + warehouse.port();
    }
}
