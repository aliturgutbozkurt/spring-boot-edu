package com.springbootedu.datajpapostgres.exercise2;

import java.math.BigDecimal;

/**
 * Exercise 2 — given: one row of the "total per customer" report.
 */
public record CustomerTotal(String customerEmail, BigDecimal total) {
}
