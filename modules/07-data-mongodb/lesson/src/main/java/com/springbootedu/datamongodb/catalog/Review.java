package com.springbootedu.datamongodb.catalog;

/**
 * Lesson 3.1 — embedded in the book document: read together, bounded in number, no own identity.
 */
public record Review(String author, int stars, String text) {
}
