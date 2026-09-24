package com.springbootedu.asyncschedulingbatch.batch;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.5 — lets the lesson simulate a crash in the middle of a job, to show a restart.
 */
@Component
public class ImportFaults {

    private volatile @Nullable String crashOnIsbn;

    public void crashOn(@Nullable String isbn) {
        this.crashOnIsbn = isbn;
    }

    void check(String isbn) {
        if (isbn.equals(crashOnIsbn)) {
            throw new IllegalStateException("simulated crash at " + isbn);    // not skippable: the job fails
        }
    }
}
