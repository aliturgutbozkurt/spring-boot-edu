package com.springbootedu.datamongodb.exercise2;

import java.time.Instant;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * Exercise 2 — revenue per category in a period, computed by MongoDB.
 */
@Service
public class SalesReport {

    private final MongoTemplate mongo;

    public SalesReport(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    public List<CategoryRevenue> revenuePerCategory(Instant from, Instant until) {
        // TODO 2a: only sales with from <= soldAt < until
        // TODO 2b: per category: units = sum of quantity, revenue = sum of quantity * unitPrice
        // TODO 2c: map _id to "category" and sort by revenue, highest first
        throw new UnsupportedOperationException("TODO 2 — " + mongo);
    }
}
