package com.springbootedu.datajdbcpostgres.book;

/**
 * Lesson 3.3 — a read model for a join: column "author_name" maps to component authorName.
 */
public record BookWithAuthor(String title, String authorName) {
}
