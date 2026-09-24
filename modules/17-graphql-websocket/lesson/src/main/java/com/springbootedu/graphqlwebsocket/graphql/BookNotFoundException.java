package com.springbootedu.graphqlwebsocket.graphql;

/**
 * Lesson 3.4 — becomes a GraphQL error of type NOT_FOUND (see {@link BookGraphQlController}).
 */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String isbn) {
        super("No book with ISBN " + isbn);
    }
}
