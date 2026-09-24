package com.springbootedu.asyncschedulingbatch.exercise3;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Given: the sales of one day.
 */
public record DailyReport(LocalDate date, int orders, BigDecimal revenue) {
}
