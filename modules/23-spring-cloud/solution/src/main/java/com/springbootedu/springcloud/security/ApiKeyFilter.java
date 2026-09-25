package com.springbootedu.springcloud.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Exercise 1 — every request to /api/** needs a valid X-Api-Key header, otherwise 401.
 */
@Component
class ApiKeyFilter implements GlobalFilter, Ordered {

    private final ApiKeys apiKeys;

    ApiKeyFilter(ApiKeys apiKeys) {
        this.apiKeys = apiKeys;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String key = exchange.getRequest().getHeaders().getFirst("X-Api-Key");
        if (path.startsWith("/api/") && (key == null || !apiKeys.isValid(key))) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();            // the request never reaches a service
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;                          // before routing and all other filters
    }
}
