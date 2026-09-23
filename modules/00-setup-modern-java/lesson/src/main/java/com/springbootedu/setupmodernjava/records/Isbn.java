package com.springbootedu.setupmodernjava.records;

/**
 * Lesson 3.1 — a compact constructor can also normalise its input.
 */
public record Isbn(String value) {

    public Isbn {
        value = value.replace("-", "").replace(" ", "");
        if (!value.matches("\\d{9}[\\dX]|\\d{13}")) {
            throw new IllegalArgumentException("Not an ISBN-10 or ISBN-13: " + value);
        }
    }
}
