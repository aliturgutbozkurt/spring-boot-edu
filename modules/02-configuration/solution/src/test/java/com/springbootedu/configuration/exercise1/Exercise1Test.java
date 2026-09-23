package com.springbootedu.configuration.exercise1;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;

/**
 * Exercise 1 — validated opening hours.
 */
class Exercise1Test {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
            .withUserConfiguration(OpeningHoursConfiguration.class)
            .withPropertyValues(
                    "bookstore.opening-hours.opens=09:00",
                    "bookstore.opening-hours.closes=21:00",
                    "bookstore.opening-hours.closed-days=SUNDAY");

    @Test
    void bindsTimesDaysAndTheDefault() {
        runner.run(context -> {
            var hours = context.getBean(OpeningHoursProperties.class);
            assertThat(hours.opens()).isEqualTo(LocalTime.of(9, 0));
            assertThat(hours.closedDays()).containsExactly(DayOfWeek.SUNDAY);
            assertThat(hours.maxReservations()).isEqualTo(3);
        });
    }

    @Test
    void knowsWhenTheShopIsOpen() {
        runner.run(context -> {
            var hours = context.getBean(OpeningHoursProperties.class);
            assertThat(hours.isOpen(DayOfWeek.MONDAY, LocalTime.of(10, 30))).isTrue();
            assertThat(hours.isOpen(DayOfWeek.MONDAY, LocalTime.of(21, 0))).isFalse();
            assertThat(hours.isOpen(DayOfWeek.MONDAY, LocalTime.of(8, 59))).isFalse();
            assertThat(hours.isOpen(DayOfWeek.SUNDAY, LocalTime.of(12, 0))).isFalse();
        });
    }

    @Test
    void closingBeforeOpeningIsRejected() {
        runner.withPropertyValues("bookstore.opening-hours.closes=08:00")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void maxReservationsMustBeBetweenOneAndTen() {
        runner.withPropertyValues("bookstore.opening-hours.max-reservations=0")
                .run(context -> assertThat(context).hasFailed());
        runner.withPropertyValues("bookstore.opening-hours.max-reservations=11")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void openingTimeIsRequired() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
                .withUserConfiguration(OpeningHoursConfiguration.class)
                .withPropertyValues("bookstore.opening-hours.closes=21:00")
                .run(context -> assertThat(context).hasFailed());
    }
}
