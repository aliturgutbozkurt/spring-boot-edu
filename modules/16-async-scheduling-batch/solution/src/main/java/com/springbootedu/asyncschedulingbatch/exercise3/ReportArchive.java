package com.springbootedu.asyncschedulingbatch.exercise3;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Given: where the reports are stored.
 */
@Component
public class ReportArchive {

    private final Map<LocalDate, DailyReport> reports = new ConcurrentHashMap<>();

    public void store(DailyReport report) {
        reports.put(report.date(), report);
    }

    public Optional<DailyReport> reportFor(LocalDate date) {
        return Optional.ofNullable(reports.get(date));
    }

    public void clear() {
        reports.clear();
    }
}
