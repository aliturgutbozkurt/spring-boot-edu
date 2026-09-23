package com.springbootedu.corecontainer.exercise2;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exercise 2 — chooses the {@link GiftWrapService} with the bookstore.gift-wrap.enabled flag.
 */
@Configuration(proxyBeanMethods = false)
public class GiftWrapConfiguration {

    @Bean
    @ConditionalOnProperty(name = "bookstore.gift-wrap.enabled", havingValue = "true")
    GiftWrapService paidGiftWrap(@Value("${bookstore.gift-wrap.price:15.00}") BigDecimal price) {
        return new PaidGiftWrap(price);
    }

    @Bean
    @ConditionalOnProperty(name = "bookstore.gift-wrap.enabled", havingValue = "false", matchIfMissing = true)
    GiftWrapService noGiftWrap() {
        return new NoGiftWrap();
    }
}
