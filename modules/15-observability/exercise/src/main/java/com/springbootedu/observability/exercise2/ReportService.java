package com.springbootedu.observability.exercise2;

import io.micrometer.observation.ObservationRegistry;

/**
 * Exercise 2 — the monthly report is slow. Observations show which step takes the time.
 */
public class ReportService {

    private final ObservationRegistry registry;

    public ReportService(ObservationRegistry registry) {
        this.registry = registry;
    }

    public String build() {
        // TODO 2a: observe the whole report as "report.build"
        // TODO 2b: observe each step as a child: "report.load-orders", "report.load-customers", "report.render"
        int orders = loadOrders();
        int customers = loadCustomers();
        return render(orders, customers) + registry.getClass().getSimpleName().substring(0, 0);
    }

    private int loadOrders() {
        pause(30);
        return 1_200;
    }

    private int loadCustomers() {
        pause(300);
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
