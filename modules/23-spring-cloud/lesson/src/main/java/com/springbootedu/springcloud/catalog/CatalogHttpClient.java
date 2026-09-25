package com.springbootedu.springcloud.catalog;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * Lesson 3.2 — an HTTP interface (Spring Framework): the proxy is built from a RestClient.
 */
// tag::http-interface[]
@HttpExchange("/api/books")
public interface CatalogHttpClient {

    @GetExchange("/{isbn}")
    Book find(@PathVariable String isbn);
}
// end::http-interface[]
