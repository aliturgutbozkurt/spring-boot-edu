package com.springbootedu.elasticsearch.search;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;

/**
 * Lessons 3.3–3.4 — full-text search with filters and highlighting, and facets with an aggregation.
 */
@Service
public class BookSearch {

    private final ElasticsearchOperations operations;

    public BookSearch(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    // tag::search[]
    public List<BookHit> search(String text, @Nullable String category, @Nullable Double maxPrice) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(bool -> {
                    bool.must(m -> m.multiMatch(match -> match
                            .query(text)
                            .fields("title^3", "description")));                // a title match counts 3×
                    if (category != null) {
                        bool.filter(f -> f.term(t -> t.field("category").value(category)));   // yes/no, no score
                    }
                    if (maxPrice != null) {
                        bool.filter(f -> f.range(r -> r.number(n -> n.field("price").lte(maxPrice))));
                    }
                    return bool;
                }))
                .withHighlightQuery(new HighlightQuery(new Highlight(List.of(
                        new HighlightField("title"), new HighlightField("description"))), BookDocument.class))
                .build();
        return operations.search(query, BookDocument.class).stream().map(BookHit::from).toList();
    }
    // end::search[]

    // tag::facets[]
    public Map<String, Long> categoryFacets(String text) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(match -> match.query(text).fields("title^3", "description")))
                .withAggregation("categories", Aggregation.of(a -> a.terms(t -> t.field("category"))))
                .withMaxResults(0)                                          // only the counts, no documents
                .build();
        SearchHits<BookDocument> hits = operations.search(query, BookDocument.class);

        var aggregations = (ElasticsearchAggregations) Objects.requireNonNull(hits.getAggregations());
        List<StringTermsBucket> buckets = Objects.requireNonNull(aggregations.get("categories"))
                .aggregation().getAggregate().sterms().buckets().array();
        Map<String, Long> counts = new LinkedHashMap<>();                  // biggest category first
        buckets.forEach(bucket -> counts.put(bucket.key().stringValue(), bucket.docCount()));
        return counts;
    }
    // end::facets[]
}
