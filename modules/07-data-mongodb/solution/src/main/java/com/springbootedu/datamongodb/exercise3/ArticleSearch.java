package com.springbootedu.datamongodb.exercise3;

import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
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
        TextQuery query = TextQuery.queryText(TextCriteria.forDefaultLanguage().matchingAny(words.split("\\s+")))
                .sortByScore();
        query.limit(limit);
        return mongo.find(query, Article.class);
    }
}
