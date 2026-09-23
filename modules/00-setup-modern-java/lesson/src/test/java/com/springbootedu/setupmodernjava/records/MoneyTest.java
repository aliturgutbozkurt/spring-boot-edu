package com.springbootedu.setupmodernjava.records;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Lesson 3.1 — records with a compact constructor.
 */
class MoneyTest {

    @Test
    void normalisesTheScale() {
        assertThat(Money.of("12.5").amount()).isEqualByComparingTo("12.50").hasScaleOf(2);
    }

    @Test
    void rejectsNegativeAmounts() {
        assertThatIllegalArgumentException().isThrownBy(() -> Money.of("-1")).withMessageContaining("negative");
    }

    @Test
    void recordsCompareByValue() {
        assertThat(Money.of("10")).isEqualTo(new Money(new BigDecimal("10.00")));
    }

    @Test
    void arithmeticReturnsNewValues() {
        var price = Money.of("40.00");
        assertThat(price.plus(Money.of("2.50"))).isEqualTo(Money.of("42.50"));
        assertThat(price.times(3)).isEqualTo(Money.of("120.00"));
        assertThat(price).isEqualTo(Money.of("40.00"));   // immutable
    }

    @Test
    void isbnIsNormalisedAndValidated() {
        assertThat(new Isbn("978-0-13-468599-1").value()).isEqualTo("9780134685991");
        assertThatIllegalArgumentException().isThrownBy(() -> new Isbn("12-34"));
    }
}
