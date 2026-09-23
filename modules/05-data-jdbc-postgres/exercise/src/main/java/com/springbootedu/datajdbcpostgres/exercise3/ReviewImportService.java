package com.springbootedu.datajdbcpostgres.exercise3;

import com.springbootedu.datajdbcpostgres.exercise2.NewReview;
import com.springbootedu.datajdbcpostgres.exercise2.ReviewRepository;
import java.util.List;
import org.springframework.stereotype.Service;

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

    // TODO 3a: all reviews are saved, or none (one transaction)
    // TODO 3b: log "SUCCESS" (with "<n> reviews") or "FAILED" (with the exception's simple class name)
    //          through ImportLog, then rethrow the failure
    public int importAll(String isbn, List<NewReview> batch) {
        for (NewReview review : batch) {
            reviews.add(review);
        }
        return batch.size();
    }
}
