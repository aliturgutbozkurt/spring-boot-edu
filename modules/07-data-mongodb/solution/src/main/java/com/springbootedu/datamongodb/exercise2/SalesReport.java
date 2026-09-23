package com.springbootedu.datamongodb.exercise2;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
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
        var pipeline = newAggregation(
                match(where("soldAt").gte(from).lt(until)),
                group("category")
                        .sum("quantity").as("units")
                        .sum(ArithmeticOperators.Multiply.valueOf("quantity").multiplyBy("unitPrice")).as("revenue"),
                project("units").and("_id").as("category").and("revenue").as("revenue"),
                sort(Sort.by(Sort.Order.desc("revenue"))));
        return mongo.aggregate(pipeline, Sale.class, CategoryRevenue.class).getMappedResults();
    }
}
