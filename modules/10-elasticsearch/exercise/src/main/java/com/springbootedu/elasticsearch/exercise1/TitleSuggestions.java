package com.springbootedu.elasticsearch.exercise1;

import java.util.List;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

/**
 * Exercise 1 — titles that match what the user has typed so far.
 */
@Service
public class TitleSuggestions {

    private final ElasticsearchOperations operations;

    public TitleSuggestions(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    public List<String> suggest(String typed, int limit) {
        // TODO 1b: a multi_match query of type bool_prefix on the title and its generated sub-fields
        // TODO 1c: at most `limit` results; return only the titles
        throw new UnsupportedOperationException("TODO 1 — " + typed + limit + operations);
    }
}
