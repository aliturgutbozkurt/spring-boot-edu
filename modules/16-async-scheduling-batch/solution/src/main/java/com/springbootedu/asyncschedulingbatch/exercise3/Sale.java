package com.springbootedu.asyncschedulingbatch.exercise3;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Given: one sale.
 */
public record Sale(LocalDate date, BigDecimal amount) {
}
