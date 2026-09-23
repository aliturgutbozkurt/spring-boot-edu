package com.springbootedu.datamongodb.catalog;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * Lessons 3.3–3.5 — MongoTemplate: dynamic queries, atomic updates, aggregations, text search.
 */
@Service
public class CatalogOperations {

    private final MongoTemplate mongo;

    public CatalogOperations(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    // tag::criteria[]
    public List<Book> search(@Nullable String category, @Nullable BigDecimal maxPrice) {
        List<Criteria> parts = new ArrayList<>();
        if (category != null) {
            parts.add(where("categories").is(category));    // "is" on an array field = "contains"
        }
        if (maxPrice != null) {
            parts.add(where("price").lte(maxPrice));
        }
        Query query = parts.isEmpty() ? new Query() : new Query(new Criteria().andOperator(parts));
        return mongo.find(query.with(Sort.by("price")), Book.class);
    }
    // end::criteria[]

    // tag::atomic-update[]
    public boolean takeFromStock(String isbn, int quantity) {
        var onlyIfEnough = new Query(where("isbn").is(isbn).and("stock").gte(quantity));
        var result = mongo.updateFirst(onlyIfEnough, new Update().inc("stock", -quantity), Book.class);
        return result.getModifiedCount() == 1;               // check and change in ONE atomic operation
    }

    public void addReview(String isbn, Review review) {
        mongo.updateFirst(new Query(where("isbn").is(isbn)), new Update().push("reviews", review), Book.class);
    }
    // end::atomic-update[]

    // tag::aggregation[]
    public List<CategoryStats> statsPerCategory() {
        var pipeline = newAggregation(
                unwind("categories"),                                           // one row per (book, category)
                group("categories").count().as("count").avg("price").as("averagePrice"),
                project("count")
                        .and("_id").as("category")
                        .and(ArithmeticOperators.Round.roundValueOf("averagePrice").place(2)).as("averagePrice"),
                sort(Sort.by(Sort.Order.desc("count"), Sort.Order.asc("category"))));
        return mongo.aggregate(pipeline, Book.class, CategoryStats.class).getMappedResults();
    }
    // end::aggregation[]

    // tag::text-search[]
    public List<Book> fullText(String words) {
        return mongo.find(TextQuery.queryText(TextCriteria.forDefaultLanguage().matchingAny(words)), Book.class);
    }
    // end::text-search[]
}
