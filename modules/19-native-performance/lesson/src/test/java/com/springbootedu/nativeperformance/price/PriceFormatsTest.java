package com.springbootedu.nativeperformance.price;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Lesson 3.4 — the implementation is chosen by class name at runtime: reflection that AOT cannot see.
 */
class PriceFormatsTest {

    @Test
    void turkishLira() {
        var formats = new PriceFormats(new PriceFormatProperties(TurkishLiraFormat.class.getName()));

        assertThat(formats.format(new BigDecimal("1234.5"))).isEqualTo("₺1.234,50");
    }

    @Test
    void euro() {
        var formats = new PriceFormats(new PriceFormatProperties(EuroFormat.class.getName()));

        assertThat(formats.format(new BigDecimal("1234.5"))).isEqualTo("1.234,50 €");
    }

    @Test
    void anUnknownClassIsAClearError() {
        assertThatThrownBy(() -> new PriceFormats(new PriceFormatProperties("com.example.Nope")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("com.example.Nope");
    }
}
