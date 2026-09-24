package com.springbootedu.asyncschedulingbatch.scheduling;

import static org.awaitility.Awaitility.await;

import com.springbootedu.asyncschedulingbatch.TestcontainersConfiguration;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Lesson 3.2 — a fixed-delay task runs again and again; the interval comes from the configuration.
 */
@SpringBootTest(properties = {"bookstore.tour.enabled=false", "bookstore.snapshot.interval=100ms"})
@Import(TestcontainersConfiguration.class)
class SnapshotSchedulingTest {

    @Autowired
    StockSnapshotTask snapshots;

    @Test
    void theSnapshotTaskRunsRepeatedly() {
        await().atMost(Duration.ofSeconds(3)).until(() -> snapshots.runs() >= 3);
    }
}
