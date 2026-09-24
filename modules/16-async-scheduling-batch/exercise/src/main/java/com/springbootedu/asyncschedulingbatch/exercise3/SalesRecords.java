package com.springbootedu.asyncschedulingbatch.exercise3;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Given: the recorded sales (in memory).
 */
@Component
public class SalesRecords {

    private final List<Sale> sales = List.of(
            new Sale(LocalDate.parse("2026-09-23"), new BigDecimal("89.90")),
            new Sale(LocalDate.parse("2026-09-23"), new BigDecimal("95.00")),
            new Sale(LocalDate.parse("2026-09-23"), new BigDecimal("55.90")),
            new Sale(LocalDate.parse("2026-09-24"), new BigDecimal("110.00")));

    public List<Sale> on(LocalDate date) {
        return sales.stream().filter(sale -> sale.date().equals(date)).toList();
    }
}
