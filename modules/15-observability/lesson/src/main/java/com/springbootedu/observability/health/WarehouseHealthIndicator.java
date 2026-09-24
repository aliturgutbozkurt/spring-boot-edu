package com.springbootedu.observability.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.1 — adds "warehouse" to /actuator/health. The bean name minus "HealthIndicator" is the component name.
 */
// tag::health[]
@Component
class WarehouseHealthIndicator implements HealthIndicator {

    private final Warehouse warehouse;

    WarehouseHealthIndicator(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public Health health() {
        return warehouse.reachable()
                ? Health.up().withDetail("location", "Istanbul").build()
                : Health.down().withDetail("reason", "no connection to the warehouse system").build();
    }
}
// end::health[]
