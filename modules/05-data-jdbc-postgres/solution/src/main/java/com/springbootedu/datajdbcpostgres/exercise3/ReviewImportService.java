package com.springbootedu.datajdbcpostgres.exercise3;

import com.springbootedu.datajdbcpostgres.exercise2.NewReview;
import com.springbootedu.datajdbcpostgres.exercise2.ReviewRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Exercise 3 — imports all reviews of a book, or none of them.
 */
@Service
public class ReviewImportService {

    private final ReviewRepository reviews;
    private final ImportLog log;

    public ReviewImportService(ReviewRepository reviews, ImportLog log) {
        this.reviews = reviews;
        this.log = log;
    }

    @Transactional
    public int importAll(String isbn, List<NewReview> batch) {
        try {
            for (NewReview review : batch) {
                reviews.add(review);
            }
        } catch (RuntimeException failure) {
            log.record(isbn, "FAILED", failure.getClass().getSimpleName());
            throw failure;                               // rethrow → this transaction rolls back
        }
        log.record(isbn, "SUCCESS", batch.size() + " reviews");
        return batch.size();
    }
}
