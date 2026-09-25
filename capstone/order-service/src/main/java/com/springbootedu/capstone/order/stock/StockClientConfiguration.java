package com.springbootedu.capstone.order.stock;

import com.springbootedu.capstone.contracts.stock.v1.StockServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelBuilderCustomizer;
import org.springframework.grpc.client.ImportGrpcClients;

/**
 * The stub of the catalog's StockService, on the channel "catalog" (spring.grpc.client.channel.catalog.target).
 */
@Configuration(proxyBeanMethods = false)
@ImportGrpcClients(target = "catalog", types = StockServiceGrpc.StockServiceBlockingStub.class)
@EnableConfigurationProperties(CatalogProperties.class)
class StockClientConfiguration {

    /**
     * gRPC keeps one long-lived HTTP/2 connection. With a DNS name that returns every catalog pod (a headless
     * Kubernetes Service), round robin spreads the calls over all of them; the default (pick first) would not.
     */
    @Bean
    <T extends ManagedChannelBuilder<T>> GrpcChannelBuilderCustomizer<T> catalogRoundRobin() {
        return GrpcChannelBuilderCustomizer.matching("catalog", builder -> builder.defaultLoadBalancingPolicy("round_robin"));
    }
}
