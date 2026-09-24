package com.springbootedu.nativeperformance.price;

import java.math.BigDecimal;

/**
 * Lesson 3.4 — one way to show a price. The implementation is configured by class name.
 */
public interface PriceFormat {

    String format(BigDecimal price);
}
