package com.springbootedu.observability.exercise2;

import static io.micrometer.observation.tck.TestObservationRegistryAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.observation.DefaultMeterObservationHandler;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.tck.TestObservationRegistry;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/**
 * Exercise 2 — instrument the report so that a trace shows where the time goes.
 */
class Exercise2Test {

    private static final List<String> STEPS = List.of("report.load-orders", "report.load-customers", "report.render");

    @Test
    void theReportAndEachStepAreObserved() {
        TestObservationRegistry registry = TestObservationRegistry.create();

        new ReportService(registry).build();

        assertThat(registry)
                .hasObservationWithNameEqualTo("report.build").that().hasBeenStarted().hasBeenStopped();
        for (String step : STEPS) {
            assertThat(registry).hasObservationWithNameEqualTo(step).that()
                    .hasParentObservationContextMatching(parent -> parent.getName().equals("report.build"));  // a child span
        }
    }

    @Test
    void theTimersShowWhichStepIsSlow() {
        var meters = new SimpleMeterRegistry();
        var registry = ObservationRegistry.create();
        registry.observationConfig().observationHandler(new DefaultMeterObservationHandler(meters));

        new ReportService(registry).build();

        String slowest = STEPS.stream()
                .max(Comparator.comparingDouble(step -> meters.get(step).timer().totalTime(TimeUnit.MILLISECONDS)))
                .orElseThrow();
        assertThat(slowest).isEqualTo("report.load-customers");
    }
}
