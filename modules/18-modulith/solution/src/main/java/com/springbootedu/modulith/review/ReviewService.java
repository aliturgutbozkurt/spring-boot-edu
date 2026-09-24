package com.springbootedu.modulith.review;

import com.springbootedu.modulith.catalog.CatalogService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;

/**
 * Exercise 1 — star ratings for books (kept in memory).
 */
@Service
public class ReviewService {

    private final CatalogService catalog;
    private final Map<String, List<Integer>> stars = new ConcurrentHashMap<>();

    ReviewService(CatalogService catalog) {
        this.catalog = catalog;
    }

    public void rate(String isbn, int stars) {
        if (!catalog.exists(isbn)) {
            throw new UnknownBookException(isbn);
        }
        this.stars.computeIfAbsent(isbn, key -> new CopyOnWriteArrayList<>()).add(stars);
    }

    public double averageOf(String isbn) {
        return stars.getOrDefault(isbn, List.of()).stream().mapToInt(Integer::intValue).average().orElse(0);
    }
}
