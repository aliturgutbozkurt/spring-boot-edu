package com.springbootedu.modulith.review;

import com.springbootedu.modulith.catalog.internal.BookRepository;
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

    // TODO 1: the review module reaches into catalog.internal — use the catalog module's API instead
    private final BookRepository books;
    private final Map<String, List<Integer>> stars = new ConcurrentHashMap<>();

    ReviewService(BookRepository books) {
        this.books = books;
    }

    public void rate(String isbn, int stars) {
        if (books.findByIsbn(isbn).isEmpty()) {
            throw new UnknownBookException(isbn);
        }
        this.stars.computeIfAbsent(isbn, key -> new CopyOnWriteArrayList<>()).add(stars);
    }

    public double averageOf(String isbn) {
        return stars.getOrDefault(isbn, List.of()).stream().mapToInt(Integer::intValue).average().orElse(0);
    }
}
