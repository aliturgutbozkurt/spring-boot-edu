package com.springbootedu.graphqlwebsocket.exercise2;

/**
 * An order line with an ISBN that is not in the catalog.
 */
public class UnknownBookException extends RuntimeException {

    public UnknownBookException(String isbn) {
        super("No book with ISBN " + isbn);
    }
}
