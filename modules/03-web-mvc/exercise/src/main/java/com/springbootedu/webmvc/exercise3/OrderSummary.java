package com.springbootedu.webmvc.exercise3;

import java.math.BigDecimal;
import java.util.List;

/**
 * Exercise 3 — given: the internal model of an order.
 */
public record OrderSummary(long id, BigDecimal total, Status status, List<Line> lines) {

    public enum Status {
        NEW("Yeni / New"), PAID("Ödendi / Paid"), SHIPPED("Kargoda / Shipped");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    public record Line(String title, int quantity) {
    }
}
