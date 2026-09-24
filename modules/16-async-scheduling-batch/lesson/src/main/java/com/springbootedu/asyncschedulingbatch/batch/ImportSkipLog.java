package com.springbootedu.asyncschedulingbatch.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.4 — a skipped line should never disappear silently.
 */
@Component
class ImportSkipLog implements SkipListener<BookLine, ImportedBook> {

    private static final Logger log = LoggerFactory.getLogger(ImportSkipLog.class);

    @Override
    public void onSkipInProcess(BookLine line, Throwable reason) {
        log.warn("skipped line {}: {}", line.isbn(), reason.getMessage());
    }

    @Override
    public void onSkipInRead(Throwable reason) {
        log.warn("skipped an unreadable line: {}", reason.getMessage());
    }
}
