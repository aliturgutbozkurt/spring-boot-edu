package com.springbootedu.nativeperformance.exercise2;

import org.springframework.stereotype.Component;

/**
 * Exercise 2 — given: a bean that makes the startup slow (for example a cache that is filled at startup).
 */
@Component
class SlowCatalogWarmup {

    SlowCatalogWarmup() throws InterruptedException {
        Thread.sleep(300);
    }
}
