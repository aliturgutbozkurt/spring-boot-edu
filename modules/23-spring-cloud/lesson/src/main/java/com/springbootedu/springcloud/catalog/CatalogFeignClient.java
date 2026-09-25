package com.springbootedu.springcloud.catalog;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Lesson 3.2 — the same client with OpenFeign: Spring Cloud builds it, load balancing included.
 */
// tag::feign[]
@FeignClient(name = "catalog-service", path = "/api/books")     // name = the service ID, resolved by the load balancer
public interface CatalogFeignClient {

    @GetMapping("/{isbn}")
    Book find(@PathVariable String isbn);
}
// end::feign[]
