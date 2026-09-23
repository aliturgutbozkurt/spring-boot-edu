package com.springbootedu.setupmodernjava.exercise2;

import java.util.List;
import java.util.Map;

/**
 * Exercise 2 — asks the stock service about many books at once.
 */
public class StockChecker {

    private final StockClient client;

    public StockChecker(StockClient client) {
        this.client = client;
    }

    public Map<String, Integer> checkAll(List<String> isbns) {
        // TODO 2a: call client.stockOf(isbn) for every ISBN in parallel, one virtual thread per call
        // TODO 2b: return the results in the SAME ORDER as the input list (hint: LinkedHashMap)
        // TODO 2c: turn checked exceptions from the futures into an IllegalStateException
        throw new UnsupportedOperationException("TODO 2 — client: " + client);
    }
}
