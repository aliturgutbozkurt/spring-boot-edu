package com.springbootedu.nativeperformance.exercise2;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.nativeperformance.NativePerformanceApplication;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

class Exercise2Test {

    @Test
    void theSlowestBeansComeFirst() {
        var startup = new BufferingApplicationStartup(10_000);
        try (var context = new SpringApplicationBuilder(NativePerformanceApplication.class)
                .applicationStartup(startup)
                .run()) {

            List<BeanTiming> slowest = StartupReport.slowestBeans(startup, 3);

            assertThat(slowest).hasSize(3);
            assertThat(slowest).isSortedAccordingTo(Comparator.comparing(BeanTiming::duration).reversed());
            assertThat(slowest.getFirst().beanName()).isEqualTo("slowCatalogWarmup");
            assertThat(slowest.getFirst().duration()).isGreaterThanOrEqualTo(Duration.ofMillis(300));
        }
    }
}
