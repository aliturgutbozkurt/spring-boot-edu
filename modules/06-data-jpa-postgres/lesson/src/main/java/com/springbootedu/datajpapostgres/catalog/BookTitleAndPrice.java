package com.springbootedu.datajpapostgres.catalog;

import java.math.BigDecimal;

/**
 * Lesson 3.4 — an interface projection: Spring Data selects only these two columns.
 */
public interface BookTitleAndPrice {

    String getTitle();

    BigDecimal getPrice();
}
