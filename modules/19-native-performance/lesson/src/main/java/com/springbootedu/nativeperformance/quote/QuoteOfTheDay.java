package com.springbootedu.nativeperformance.quote;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.core.io.ClassPathResource;

/**
 * Lesson 3.4 — reads a classpath resource. A native image contains only the resources a hint includes.
 */
public class QuoteOfTheDay {

    private final Clock clock;
    private final List<String> quotes;

    public QuoteOfTheDay(Clock clock) {
        this.clock = clock;
        this.quotes = load();
    }

    public String today() {
        long day = LocalDate.now(clock).toEpochDay();
        return quotes.get((int) (day % quotes.size()));
    }

    public int count() {
        return quotes.size();
    }

    private static List<String> load() {
        try (InputStream in = new ClassPathResource("quotes/quotes.txt").getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8).lines().filter(line -> !line.isBlank()).toList();
        } catch (IOException e) {
            throw new UncheckedIOException("quotes/quotes.txt is missing — in a native image: is there a resource hint?", e);
        }
    }
}
