package com.springbootedu.springcloud.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Exercise 1 — every request to /api/** needs a valid X-Api-Key header, otherwise 401.
 */
@Component
class ApiKeyFilter implements GlobalFilter, Ordered {

    @SuppressWarnings("unused")                                 // used once TODO 1b is done
    private final ApiKeys apiKeys;

    ApiKeyFilter(ApiKeys apiKeys) {
        this.apiKeys = apiKeys;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // TODO 1b: a request to /api/** without a valid X-Api-Key header (apiKeys.isValid) gets 401 and goes no further
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;                          // before routing and all other filters
    }
}
