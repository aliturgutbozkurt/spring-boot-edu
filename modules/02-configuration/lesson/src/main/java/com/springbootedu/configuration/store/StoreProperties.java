package com.springbootedu.configuration.store;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Currency;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * Lesson 3.2 — every "bookstore.store.*" property in one immutable, validated record.
 *
 * @param name         shop name shown to customers
 * @param supportEmail where customers write to
 * @param currency     prices are shown in this currency
 * @param categories   book categories on the home page
 * @param shipping     shipping rules
 * @param banner       optional message at the top of every page
 */
// tag::store-properties[]
@ConfigurationProperties("bookstore.store")
@Validated                                               // validate at startup — fail fast
public record StoreProperties(
        @NotBlank String name,
        @NotBlank @Email String supportEmail,
        @DefaultValue("TRY") Currency currency,          // "TRY" → java.util.Currency, converted by Boot
        @DefaultValue List<String> categories,           // empty list instead of null
        @Valid @NotNull Shipping shipping,               // @Valid: validate the nested object as well
        @Nullable String banner) {

    /**
     * @param freeFrom     orders from this amount ship for free
     * @param deliveryTime promised delivery time, e.g. 2d or 36h
     */
    public record Shipping(
            @NotNull @DecimalMin("0") BigDecimal freeFrom,
            @DefaultValue("3d") Duration deliveryTime) {  // "2d", "36h", "PT12H" all work
    }
}
// end::store-properties[]
