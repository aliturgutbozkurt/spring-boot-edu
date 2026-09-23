package com.springbootedu.corecontainer.lifecycle;

import java.time.Clock;
import java.time.Instant;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.4 — {@link SmartLifecycle}: start after all beans are ready, stop before they are destroyed.
 */
// tag::smart-lifecycle[]
@Component
public class InventorySync implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(InventorySync.class);

    private final Clock clock;
    private volatile boolean running;
    private volatile @Nullable Instant startedAt;

    public InventorySync(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void start() {                     // called once the context has refreshed
        startedAt = clock.instant();
        running = true;
        log.info("Stok senkronizasyonu başladı / Inventory sync started");
    }

    @Override
    public void stop() {                      // called when the context closes
        running = false;
        log.info("Stok senkronizasyonu durdu / Inventory sync stopped");
    }

    @Override
    public boolean isRunning() {
        return running;
    }
    // end::smart-lifecycle[]

    public @Nullable Instant startedAt() {
        return startedAt;
    }
}
