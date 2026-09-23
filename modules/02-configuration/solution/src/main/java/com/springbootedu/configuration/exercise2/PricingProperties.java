package com.springbootedu.configuration.exercise2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Exercise 2 — given: prices under "bookstore.pricing". The values live in the YAML files.
 *
 * @param shippingFee     added to every order
 * @param discountPercent taken off the book price
 */
@ConfigurationProperties("bookstore.pricing")
public record PricingProperties(BigDecimal shippingFee, int discountPercent) {

    public BigDecimal total(BigDecimal bookPrice) {
        BigDecimal discounted = bookPrice.multiply(BigDecimal.valueOf(100 - discountPercent)).movePointLeft(2);
        return discounted.add(shippingFee).setScale(2, RoundingMode.HALF_UP);
    }
}
