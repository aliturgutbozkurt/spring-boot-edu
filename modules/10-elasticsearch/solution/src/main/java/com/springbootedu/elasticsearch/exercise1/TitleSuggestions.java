package com.springbootedu.elasticsearch.exercise1;

import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import java.util.List;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
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
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(match -> match
                        .query(typed)
                        .type(TextQueryType.BoolPrefix)
                        .fields("title", "title._2gram", "title._3gram")))
                .withMaxResults(limit)
                .build();
        return operations.search(query, TitleDocument.class).stream()
                .map(SearchHit::getContent)
                .map(TitleDocument::title)
                .toList();
    }
}
