package com.springbootedu.elasticsearch.exercise2;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

/**
 * Exercise 2 — search with facets that stay useful after a category was chosen.
 */
@Service
public class FacetedSearch {

    private final ElasticsearchOperations operations;

    public FacetedSearch(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    public SearchPage search(String text, @Nullable String category, @Nullable Double maxPrice) {
        NativeQueryBuilder builder = NativeQuery.builder()
                .withQuery(q -> q.bool(bool -> {
                    bool.must(m -> m.match(match -> match.field("name").query(text)));
                    if (maxPrice != null) {                                     // affects hits AND counts
                        bool.filter(f -> f.range(r -> r.number(n -> n.field("price").lte(maxPrice))));
                    }
                    return bool;
                }))
                .withAggregation("categories", Aggregation.of(a -> a.terms(t -> t.field("category"))));
        if (category != null) {
            builder.withFilter(Query.of(f -> f.term(t -> t.field("category").value(category))));   // post_filter:
        }                                                                                           // hits only
        SearchHits<ProductDocument> result = operations.search(builder.build(), ProductDocument.class);

        var aggregations = (ElasticsearchAggregations) Objects.requireNonNull(result.getAggregations());
        Map<String, Long> counts = new LinkedHashMap<>();
        Objects.requireNonNull(aggregations.get("categories")).aggregation().getAggregate().sterms().buckets().array()
                .forEach(bucket -> counts.put(bucket.key().stringValue(), bucket.docCount()));
        return new SearchPage(result.stream().map(SearchHit::getContent).toList(), counts);
    }
}
