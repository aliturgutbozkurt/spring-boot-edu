package com.springbootedu.grpc.client;

import com.springbootedu.grpc.catalog.v1.BookCatalogGrpc;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ImportGrpcClients;

/**
 * Lesson 3.6 — creates stub beans for the channel "catalog" (spring.grpc.client.channel.catalog.*).
 */
// tag::import-clients[]
@Configuration(proxyBeanMethods = false)
@ImportGrpcClients(target = "catalog", types = {
        BookCatalogGrpc.BookCatalogBlockingStub.class, BookCatalogGrpc.BookCatalogStub.class})
class ClientConfiguration {
}
// end::import-clients[]
