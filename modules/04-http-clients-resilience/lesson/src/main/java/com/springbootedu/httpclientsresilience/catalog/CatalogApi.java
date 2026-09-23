package com.springbootedu.httpclientsresilience.catalog;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * Lesson 3.4 — an HTTP interface: Spring generates the implementation from the annotations.
 */
// tag::http-interface[]
@HttpExchange("/catalog/books")
public interface CatalogApi {

    @GetExchange("/{isbn}")
    BookInfo find(@PathVariable String isbn);

    @GetExchange("/{isbn}/price")
    Price price(@PathVariable String isbn);

    @GetExchange("/{isbn}/cover")
    byte[] cover(@PathVariable String isbn);
}
// end::http-interface[]
