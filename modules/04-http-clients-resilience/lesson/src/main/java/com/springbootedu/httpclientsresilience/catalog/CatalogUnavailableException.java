package com.springbootedu.httpclientsresilience.catalog;

/**
 * Lesson 3.2 — the remote catalog failed (HTTP 5xx); trying again later may help.
 */
public class CatalogUnavailableException extends RuntimeException {

    public CatalogUnavailableException(String message) {
        super(message);
    }
}
