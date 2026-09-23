package com.springbootedu.webmvc.exercise1;

import org.jspecify.annotations.Nullable;

/**
 * Exercise 1 — request body for creating an author.
 *
 * @param name    author name, not blank
 * @param country ISO 3166 alpha-2 country code, e.g. TR
 */
public record AuthorRequest(
        @Nullable String name,           // TODO 1d: must not be blank
        @Nullable String country) {      // TODO 1d: exactly two upper-case letters, e.g. "TR"
}
