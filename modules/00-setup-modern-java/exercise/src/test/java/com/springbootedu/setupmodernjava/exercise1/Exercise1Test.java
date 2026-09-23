package com.springbootedu.setupmodernjava.exercise1;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Exercise 1 — discount rates with pattern matching and guards.
 */
class Exercise1Test {

    @Test
    void regularCustomersPayFullPrice() {
        assertThat(DiscountPolicy.rateFor(new Regular())).isZero();
    }

    @Test
    void studentsGetTenPercent() {
        assertThat(DiscountPolicy.rateFor(new Student("ODTÜ"))).isEqualTo(10);
    }

    @Test
    void membersGetMoreTheLongerTheyStay() {
        assertThat(DiscountPolicy.rateFor(new Member(0))).isEqualTo(5);
        assertThat(DiscountPolicy.rateFor(new Member(1))).isEqualTo(5);
        assertThat(DiscountPolicy.rateFor(new Member(2))).isEqualTo(15);
        assertThat(DiscountPolicy.rateFor(new Member(4))).isEqualTo(15);
        assertThat(DiscountPolicy.rateFor(new Member(5))).isEqualTo(20);
    }

    @Test
    void priceAppliesTheRate() {
        assertThat(DiscountPolicy.priceFor(new Student("İTÜ"), new BigDecimal("80.00"))).isEqualByComparingTo("72.00");
        assertThat(DiscountPolicy.priceFor(new Regular(), new BigDecimal("80.00"))).isEqualByComparingTo("80.00");
    }
}
