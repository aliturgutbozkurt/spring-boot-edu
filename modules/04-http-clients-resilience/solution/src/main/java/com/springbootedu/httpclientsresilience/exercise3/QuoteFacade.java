package com.springbootedu.httpclientsresilience.exercise3;

import org.springframework.resilience.InvocationRejectedException;
import org.springframework.stereotype.Service;

/**
 * Exercise 3 — tries express first; when the express slot is busy it uses a standard quote.
 */
@Service
public class QuoteFacade {

    private final QuoteService quotes;

    public QuoteFacade(QuoteService quotes) {
        this.quotes = quotes;
    }

    public String expressOrStandard(String isbn) {
        try {
            quotes.expressQuote(isbn);
            return "express";
        } catch (InvocationRejectedException busy) {
            quotes.standardQuote(isbn);
            return "standard";
        }
    }
}
