package com.springbootedu.datajdbcpostgres.exercise2;

import java.time.Instant;

/**
 * Exercise 2 — given: a stored review.
 */
public record Review(long id, int stars, String comment, Instant createdAt) {
}
