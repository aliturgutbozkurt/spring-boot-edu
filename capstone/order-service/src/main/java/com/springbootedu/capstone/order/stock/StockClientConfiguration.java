package com.springbootedu.capstone.order.stock;

import com.springbootedu.capstone.contracts.stock.v1.StockServiceGrpc;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ImportGrpcClients;

/**
 * The stub of the catalog's StockService, on the channel "catalog" (spring.grpc.client.channel.catalog.target).
 */
@Configuration(proxyBeanMethods = false)
@ImportGrpcClients(target = "catalog", types = StockServiceGrpc.StockServiceBlockingStub.class)
@EnableConfigurationProperties(CatalogProperties.class)
class StockClientConfiguration {
}
