package com.springbootedu.datajdbcpostgres.book;

import java.math.BigDecimal;
import org.jspecify.annotations.Nullable;

/**
 * A row of the "book" table. JdbcClient maps columns to record components by name.
 */
public record Book(@Nullable Long id, String isbn, String title, BigDecimal price, int stock) {
}
