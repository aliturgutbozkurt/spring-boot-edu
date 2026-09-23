package com.springbootedu.corecontainer.exercise2;

import org.springframework.context.annotation.Configuration;

/**
 * Exercise 2 — chooses the {@link GiftWrapService} with the bookstore.gift-wrap.enabled flag.
 */
@Configuration(proxyBeanMethods = false)
public class GiftWrapConfiguration {

    // TODO 2a: add a @Bean method that returns a PaidGiftWrap when bookstore.gift-wrap.enabled=true
    // TODO 2b: its price comes from bookstore.gift-wrap.price, 15.00 when the property is missing
    // TODO 2c: add a @Bean method that returns a NoGiftWrap when the flag is false OR missing
}
