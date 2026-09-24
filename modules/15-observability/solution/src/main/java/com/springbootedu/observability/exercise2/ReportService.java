package com.springbootedu.observability.exercise2;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import java.util.function.Supplier;

/**
 * Exercise 2 — the monthly report is slow. Observations show which step takes the time.
 */
public class ReportService {

    private final ObservationRegistry registry;

    public ReportService(ObservationRegistry registry) {
        this.registry = registry;
    }

    public String build() {
        return Observation.createNotStarted("report.build", registry).observe(() -> {
            int orders = observe("report.load-orders", this::loadOrders);
            int customers = observe("report.load-customers", this::loadCustomers);
            return observe("report.render", () -> render(orders, customers));
        });
    }

    private <T> T observe(String name, Supplier<T> step) {
        return Observation.createNotStarted(name, registry).observe(step);   // a child of the current observation
    }

    private int loadOrders() {
        pause(30);
        return 1_200;
    }

    private int loadCustomers() {
        pause(300);                                                          // the slow one
        return 340;
    }

    private String render(int orders, int customers) {
        pause(20);
        return orders + " orders from " + customers + " customers";
    }

    private static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
