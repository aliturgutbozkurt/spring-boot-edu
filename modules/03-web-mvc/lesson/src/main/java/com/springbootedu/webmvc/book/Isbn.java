package com.springbootedu.webmvc.book;

/**
 * A value object: 13 digits, hyphens removed. In JSON it is a plain string (see IsbnJacksonComponent).
 */
public record Isbn(String value) {

    public Isbn {
        value = value.replace("-", "").replace(" ", "");
        if (!value.matches("\\d{13}")) {
            throw new IllegalArgumentException("ISBN-13 expected: " + value);
        }
    }
}
