package com.springbootedu.configuration.exercise1;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Exercise 1 — the shop's opening hours under "bookstore.opening-hours".
 *
 * @param opens           opening time, e.g. 09:00
 * @param closes          closing time, must be after {@code opens}
 * @param maxReservations books a customer may reserve at once (1..10, default 3)
 * @param closedDays      days the shop stays closed
 */
@ConfigurationProperties("bookstore.opening-hours")
// TODO 1a: validate this record at startup
public record OpeningHoursProperties(
        LocalTime opens,                 // TODO 1b: required
        LocalTime closes,                // TODO 1b: required
        int maxReservations,             // TODO 1c: 1..10, 3 when not configured
        List<DayOfWeek> closedDays) {    // TODO 1d: an empty list when not configured

    // TODO 1e: reject configurations where "closes" is not after "opens"
    //          (hint: a boolean method with @AssertTrue)

    public boolean isOpen(DayOfWeek day, LocalTime time) {
        // TODO 1f: open on days that are not closed, from "opens" (inclusive) to "closes" (exclusive)
        throw new UnsupportedOperationException("TODO 1f");
    }
}
