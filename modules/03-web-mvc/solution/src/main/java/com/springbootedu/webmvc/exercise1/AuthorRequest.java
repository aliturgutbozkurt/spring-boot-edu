package com.springbootedu.webmvc.exercise1;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Exercise 1 — request body for creating an author.
 *
 * @param name    author name, not blank
 * @param country ISO 3166 alpha-2 country code, e.g. TR
 */
public record AuthorRequest(
        @NotBlank @Nullable String name,
        @NotBlank @Pattern(regexp = "[A-Z]{2}") @Nullable String country) {
}
