package com.springbootedu.setupmodernjava.sealed;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.setupmodernjava.records.Money;
import org.junit.jupiter.api.Test;

/**
 * Lesson 3.2 — sealed types and an exhaustive switch.
 */
class DiscountTest {

    @Test
    void percentage() {
        assertThat(Discounts.apply(new PercentageDiscount(10), Money.of("200"))).isEqualTo(Money.of("180.00"));
    }

    @Test
    void fixedAmountNeverGoesBelowZero() {
        assertThat(Discounts.apply(new FixedDiscount(Money.of("30")), Money.of("100"))).isEqualTo(Money.of("70.00"));
        assertThat(Discounts.apply(new FixedDiscount(Money.of("30")), Money.of("20"))).isEqualTo(Money.of("0.00"));
    }

    @Test
    void noDiscount() {
        assertThat(Discounts.apply(new NoDiscount(), Money.of("55"))).isEqualTo(Money.of("55.00"));
    }

    @Test
    void describesEveryKind() {
        assertThat(Discounts.describe(new PercentageDiscount(15))).isEqualTo("%15 indirim / 15% off");
        assertThat(Discounts.describe(new FixedDiscount(Money.of("25")))).isEqualTo("25.00 TL indirim / 25.00 TRY off");
        assertThat(Discounts.describe(new NoDiscount())).isEqualTo("İndirim yok / No discount");
    }
}
