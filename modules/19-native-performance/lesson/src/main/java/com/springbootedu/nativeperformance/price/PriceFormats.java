package com.springbootedu.nativeperformance.price;

import java.math.BigDecimal;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.4 — loads the configured PriceFormat by name. On the JVM this always works; in a native image
 * the class and its constructor exist only if a reflection hint names them (see BookstoreRuntimeHints).
 */
@Component
@EnableConfigurationProperties(PriceFormatProperties.class)
public class PriceFormats {

    private final PriceFormat format;

    public PriceFormats(PriceFormatProperties properties) {
        this.format = instantiate(properties.className());
    }

    public String format(BigDecimal price) {
        return format.format(price);
    }

    // tag::reflection[]
    private static PriceFormat instantiate(String className) {
        try {
            Class<?> type = Class.forName(className);                       // invisible for static analysis
            return (PriceFormat) type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot create price format " + className, e);
        }
    }
    // end::reflection[]
}
