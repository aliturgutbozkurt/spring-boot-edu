package com.springbootedu.datajdbcpostgres.exercise2;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * Exercise 2 — reviews with JdbcClient; batches with NamedParameterJdbcTemplate.
 */
@Repository
public class ReviewRepository {

    // Hint: the book id can be looked up inside the INSERT: (select id from book where isbn = :isbn)

    private final JdbcClient jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;

    public ReviewRepository(JdbcClient jdbc, NamedParameterJdbcTemplate namedJdbc) {
        this.jdbc = jdbc;
        this.namedJdbc = namedJdbc;
    }

    public long add(NewReview review) {
        // TODO 2a: insert the review and return the generated id
        throw new UnsupportedOperationException("TODO 2a — " + jdbc);
    }

    public List<Review> findByIsbn(String isbn) {
        // TODO 2b: all reviews of the book, newest first, mapped to Review records
        throw new UnsupportedOperationException("TODO 2b");
    }

    public Optional<Double> averageStars(String isbn) {
        // TODO 2c: the average stars of the book; empty when it has no reviews
        throw new UnsupportedOperationException("TODO 2c");
    }

    public int[] addAll(List<NewReview> reviews) {
        // TODO 2d: insert all reviews in ONE batch round-trip and return the update counts
        throw new UnsupportedOperationException("TODO 2d — " + namedJdbc);
    }
}
