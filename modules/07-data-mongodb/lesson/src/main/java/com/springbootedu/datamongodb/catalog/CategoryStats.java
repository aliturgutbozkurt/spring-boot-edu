package com.springbootedu.datamongodb.catalog;

import java.math.BigDecimal;

/**
 * Lesson 3.4 — one result row of the aggregation.
 */
public record CategoryStats(String category, long count, BigDecimal averagePrice) {
}
