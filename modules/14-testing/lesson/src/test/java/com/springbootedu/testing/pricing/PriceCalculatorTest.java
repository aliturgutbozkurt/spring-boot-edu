package com.springbootedu.testing.pricing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Lesson 3.1 — a unit test: no Spring, no database, milliseconds. The base of the test pyramid.
 */
// tag::unit-test[]
class PriceCalculatorTest {

    private final PriceCalculator calculator = new PriceCalculator();

    @ParameterizedTest(name = "{1} × {0} = {2}")
    @CsvSource({
            "10.00,  1,  10.00",      // no discount
            "10.00,  4,  40.00",
            "10.00,  5,  47.50",      // 5 % from 5 copies
            "10.00, 10,  90.00",      // 10 % from 10 copies
            "89.90,  3, 269.70",
            " 9.99,  5,  47.45"})     // 49.95 − 2.4975 = 47.4525 → rounded half up
    void appliesQuantityDiscounts(String unitPrice, int quantity, String expected) {
        assertThat(calculator.total(new BigDecimal(unitPrice), quantity)).isEqualByComparingTo(expected);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejectsQuantitiesBelowOne(int quantity) {
        assertThatIllegalArgumentException().isThrownBy(() -> calculator.total(BigDecimal.TEN, quantity));
    }
}
// end::unit-test[]
