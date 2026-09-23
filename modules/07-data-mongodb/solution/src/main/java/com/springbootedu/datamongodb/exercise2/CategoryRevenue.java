package com.springbootedu.datamongodb.exercise2;

import java.math.BigDecimal;

/**
 * Exercise 2 — given: one row of the report.
 */
public record CategoryRevenue(String category, long units, BigDecimal revenue) {
}
