package com.springbootedu.capstone.gateway.routing;

import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.Optional;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * ADR-7 — whose bucket does a request use? A signed-in customer's own (the JWT subject), so one customer
 * cannot slow down the others; an anonymous request the bucket of its IP address.
 */
@Configuration(proxyBeanMethods = false)
class RateLimitConfiguration {

    // tag::key-resolver[]
    @Bean
    KeyResolver customerOrIpKeyResolver() {
        return exchange -> exchange.getPrincipal()
                .map(Principal::getName)
                .map(subject -> "customer:" + subject)
                .switchIfEmpty(Mono.fromSupplier(() -> "ip:" + Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                        .map(InetSocketAddress::getHostString).orElse("unknown")));
    }
}
    // end::key-resolver[]
