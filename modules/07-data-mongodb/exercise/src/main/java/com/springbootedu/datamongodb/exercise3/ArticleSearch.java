package com.springbootedu.datamongodb.exercise3;

import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * Exercise 3 — the best matches first.
 */
@Service
public class ArticleSearch {

    private final MongoTemplate mongo;

    public ArticleSearch(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    public List<Article> search(String words, int limit) {
        // TODO 3b: a text query matching ANY of the words (split on spaces),
        //          sorted by relevance score, at most "limit" results
        throw new UnsupportedOperationException("TODO 3b — " + mongo);
    }
}
