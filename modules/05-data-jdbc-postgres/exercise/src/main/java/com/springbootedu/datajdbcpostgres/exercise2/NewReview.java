package com.springbootedu.datajdbcpostgres.exercise2;

/**
 * Exercise 2 — given: a review to be stored.
 */
public record NewReview(String isbn, int stars, String comment) {
}
