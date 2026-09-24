package com.springbootedu.nativeperformance.price;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Lesson 3.4 — "₺1.234,50". Created by reflection only, so no code calls its constructor directly.
 */
public class TurkishLiraFormat implements PriceFormat {

    @Override
    public String format(BigDecimal price) {
        var pattern = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.of("tr", "TR")));
        return "₺" + pattern.format(price);
    }
}
