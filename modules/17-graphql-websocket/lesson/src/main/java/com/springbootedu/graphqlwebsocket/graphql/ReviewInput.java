package com.springbootedu.graphqlwebsocket.graphql;

/**
 * Lesson 3.4 — the schema's {@code input ReviewInput}; Spring GraphQL binds the argument map to this record.
 */
public record ReviewInput(String isbn, int stars, String text) {
}
