package com.springbootedu.dockerdeployment.shop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Lesson 3.5 — 12-factor config: the same image runs everywhere, the environment sets the values.
 * BOOKSTORE_SHOP_NAME=Ankara → bookstore.shop.name (relaxed binding).
 */
// tag::properties[]
@ConfigurationProperties("bookstore.shop")
public record ShopProperties(@DefaultValue("Bookstore") String name, @DefaultValue("TRY") String currency) {
}
// end::properties[]
