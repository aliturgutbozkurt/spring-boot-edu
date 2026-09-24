package com.springbootedu.observability.health;

import org.springframework.stereotype.Component;

/**
 * Lesson 3.1 — stands in for an external warehouse system that can be reachable or not.
 */
@Component
public class Warehouse {

    private volatile boolean reachable = true;

    public boolean reachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }
}
