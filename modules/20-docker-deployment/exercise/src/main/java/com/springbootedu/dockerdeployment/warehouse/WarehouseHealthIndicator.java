package com.springbootedu.dockerdeployment.warehouse;

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
        // TODO 3a: open a TCP connection to warehouse.host():warehouse.port() (timeout 500 ms):
        //          UP if it works, DOWN with the exception if not; add the address as a detail
        return Health.unknown().withDetail("todo", "3a " + warehouse).build();
    }
}
