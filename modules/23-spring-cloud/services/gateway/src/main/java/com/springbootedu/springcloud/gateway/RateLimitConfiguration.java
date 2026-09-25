package com.springbootedu.springcloud.gateway;

import java.net.InetSocketAddress;
import java.util.Optional;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Lesson 3.1 — the rate limiter counts per key: the customer (header X-Customer), otherwise the IP address.
 */
// tag::key-resolver[]
@Configuration(proxyBeanMethods = false)
class RateLimitConfiguration {

    @Bean
    KeyResolver clientIpKeyResolver() {
        return exchange -> Mono.just(Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("X-Customer"))
                .or(() -> Optional.ofNullable(exchange.getRequest().getRemoteAddress()).map(InetSocketAddress::getHostString))
                .orElse("unknown"));
    }
}
// end::key-resolver[]
