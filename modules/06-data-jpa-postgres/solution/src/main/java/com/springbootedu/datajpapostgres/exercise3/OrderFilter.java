package com.springbootedu.datajpapostgres.exercise3;

import com.springbootedu.datajpapostgres.exercise1.OrderStatus;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * Exercise 3 — given: optional search criteria.
 */
public record OrderFilter(@Nullable String customerEmail, @Nullable OrderStatus status,
                          @Nullable Instant createdAfter, @Nullable String containsIsbn) {
}
