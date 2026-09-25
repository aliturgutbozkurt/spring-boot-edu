package com.springbootedu.springcloud.catalog;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Lesson 3.3 — a RestClient that resolves "http://catalog-service" with Spring Cloud LoadBalancer.
 */
// tag::load-balanced[]
@Configuration(proxyBeanMethods = false)
class CatalogClientConfiguration {

    @Bean
    @LoadBalanced                                        // the host name is a service ID, not a DNS name
    RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    CatalogHttpClient catalogHttpClient(RestClient.Builder loadBalancedRestClientBuilder) {
        RestClient restClient = loadBalancedRestClientBuilder.baseUrl("http://catalog-service").build();
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build()
                .createClient(CatalogHttpClient.class);
    }
}
// end::load-balanced[]
