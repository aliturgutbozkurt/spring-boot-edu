package com.springbootedu.elasticsearch.exercise3;

import java.util.List;

/**
 * Given: the source of truth for the catalog (in a real application: the database).
 */
public interface BookSource {

    List<CatalogBook> allBooks();
}
