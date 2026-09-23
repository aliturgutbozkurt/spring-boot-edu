package com.springbootedu.corecontainer.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.corecontainer.book.InMemoryBookCatalog;
import java.time.Clock;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Lesson 3.4 — init/destroy callbacks and SmartLifecycle.
 */
class LifecycleTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withBean(InMemoryBookCatalog.class)
            .withBean(TitleIndex.class)
            .withBean(Clock.class, Clock::systemUTC)
            .withBean(InventorySync.class);

    @Test
    void postConstructBuildsTheIndexAfterInjection() {
        runner.run(context -> assertThat(context.getBean(TitleIndex.class).search("java"))
                .containsExactlyInAnyOrder("Effective Java", "Java Puzzlers", "Modern Java in Action"));
    }

    @Test
    void preDestroyClearsTheIndexWhenTheContextCloses() {
        var index = new TitleIndex[1];
        runner.run(context -> index[0] = context.getBean(TitleIndex.class));
        assertThat(index[0].size()).isZero();
    }

    @Test
    void smartLifecycleRunsWhileTheContextIsRunning() {
        var sync = new InventorySync[1];
        runner.run(context -> {
            sync[0] = context.getBean(InventorySync.class);
            assertThat(sync[0].isRunning()).isTrue();
        });
        assertThat(sync[0].isRunning()).isFalse();
    }
}
