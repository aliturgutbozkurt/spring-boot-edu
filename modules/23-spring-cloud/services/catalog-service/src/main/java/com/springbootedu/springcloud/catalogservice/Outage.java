package com.springbootedu.springcloud.catalogservice;

import java.time.Duration;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.4 — switches the instance into trouble, to see the circuit breaker open: errors or slow answers.
 */
@Component
public class Outage {

    private volatile boolean failing;
    private volatile Duration latency = Duration.ZERO;

    public void set(boolean failing, Duration latency) {
        this.failing = failing;
        this.latency = latency;
    }

    public boolean failing() {
        return failing;
    }

    public Duration latency() {
        return latency;
    }
}
