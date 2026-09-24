package com.springbootedu.asyncschedulingbatch.exercise1;

import java.math.BigDecimal;

/**
 * Given: a shipping price offered by a carrier.
 */
public record Quote(String carrier, BigDecimal price) {
}
