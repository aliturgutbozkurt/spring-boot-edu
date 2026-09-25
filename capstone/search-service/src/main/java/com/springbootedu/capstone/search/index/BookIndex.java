package com.springbootedu.capstone.search.index;

import com.springbootedu.capstone.contracts.events.BookChanged;
import com.springbootedu.capstone.contracts.events.OrderPlaced;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Keeps the read model up to date. Both methods are called by event listeners (C.4) and must be idempotent:
 * Kafka delivers at least once (ADR-3). Every change evicts the cached search results (ADR-7).
 */
@Component
public class BookIndex {

    private static final IndexCoordinates BOOKS = IndexCoordinates.of("books");
    private static final Duration REMEMBER_SALES = Duration.ofDays(7);   // longer than any redelivery

    private final ElasticsearchOperations elasticsearch;
    private final StringRedisTemplate redis;

    BookIndex(ElasticsearchOperations elasticsearch, StringRedisTemplate redis) {
        this.elasticsearch = elasticsearch;
        this.redis = redis;
    }

    /**
     * Creates or updates a book. A partial update touches only the catalog's fields, so the sales count survives;
     * the upsert document is used when the book is new. Applying the same event twice gives the same result.
     */
    @CacheEvict(cacheNames = "search", allEntries = true)
    public void index(BookChanged book) {
        Map<String, Object> catalogFields = new LinkedHashMap<>();
        catalogFields.put("title", book.title());
        catalogFields.put("authors", book.authors());
        catalogFields.put("description", book.description());
        catalogFields.put("price", book.price().doubleValue());
        catalogFields.put("stock", book.stock());

        Map<String, Object> newBook = new LinkedHashMap<>(catalogFields);
        newBook.put("sold", 0L);

        elasticsearch.update(UpdateQuery.builder(book.isbn())
                .withDocument(Document.from(catalogFields))
                .withUpsert(Document.from(newBook))
                .withRefreshPolicy(RefreshPolicy.IMMEDIATE)     // searchable at once: the cache is evicted now
                .build(), BOOKS);
    }

    /**
     * Adds the quantities of an order to the books' sales. Redis remembers which orders were counted
     * (SET NX), so a redelivered event changes nothing.
     */
    // tag::record-sale[]
    @CacheEvict(cacheNames = "search", allEntries = true)
    public void recordSale(OrderPlaced order) {
        String mark = "search:sale-counted:" + order.orderId();
        if (!Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(mark, "1", REMEMBER_SALES))) {
            return;                                             // counted before
        }
        try {
            order.lines().forEach(line -> elasticsearch.update(UpdateQuery.builder(line.isbn())
                    .withScript("ctx._source.sold += params.quantity")    // atomic inside Elasticsearch
                    .withParams(Map.of("quantity", line.quantity()))
                    .withRefreshPolicy(RefreshPolicy.IMMEDIATE)
                    .build(), BOOKS));
        } catch (RuntimeException e) {
            // let the redelivery try again. Caveat: lines updated before the failure are counted twice then —
            // exact counting would need one document per order (or per order line) instead of a counter
            redis.delete(mark);
            throw e;
        }
    }
}
    // end::record-sale[]
