package com.springbootedu.configuration.exercise1;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * Exercise 1 — the shop's opening hours under "bookstore.opening-hours".
 *
 * @param opens           opening time, e.g. 09:00
 * @param closes          closing time, must be after {@code opens}
 * @param maxReservations books a customer may reserve at once (1..10, default 3)
 * @param closedDays      days the shop stays closed
 */
@ConfigurationProperties("bookstore.opening-hours")
@Validated
public record OpeningHoursProperties(
        @NotNull LocalTime opens,
        @NotNull LocalTime closes,
        @DefaultValue("3") @Min(1) @Max(10) int maxReservations,
        @DefaultValue List<DayOfWeek> closedDays) {

    @AssertTrue(message = "closes must be after opens")
    public boolean isClosingAfterOpening() {
        return opens == null || closes == null || closes.isAfter(opens);
    }

    public boolean isOpen(DayOfWeek day, LocalTime time) {
        return !closedDays.contains(day) && !time.isBefore(opens) && time.isBefore(closes);
    }
}
