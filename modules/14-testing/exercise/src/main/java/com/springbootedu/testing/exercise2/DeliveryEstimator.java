package com.springbootedu.testing.exercise2;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Exercise 2 — given: when will an order placed "today" arrive? Three business days, weekends do not count.
 */
public class DeliveryEstimator {

    private final Clock clock;

    public DeliveryEstimator(Clock clock) {
        this.clock = clock;
    }

    public LocalDate deliveryDate() {
        LocalDate date = LocalDate.now(clock);
        int businessDays = 0;
        while (businessDays < 3) {
            date = date.plusDays(1);
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                businessDays++;
            }
        }
        return date;
    }
}
