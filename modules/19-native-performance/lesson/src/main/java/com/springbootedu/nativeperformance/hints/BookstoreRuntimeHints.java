package com.springbootedu.nativeperformance.hints;

import com.springbootedu.nativeperformance.price.EuroFormat;
import com.springbootedu.nativeperformance.price.TurkishLiraFormat;
import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * Lesson 3.4 — tells the AOT engine what the code needs at runtime but static analysis cannot find.
 * The hints end up in META-INF/native-image/.../reachability-metadata.json.
 */
// tag::hints[]
public class BookstoreRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        // PriceFormats creates these by name: keep the classes and allow calling their constructors
        hints.reflection()
                .registerType(TurkishLiraFormat.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
                .registerType(EuroFormat.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
        // QuoteOfTheDay reads this file: include it in the image
        hints.resources().registerPattern("quotes/*.txt");
    }

    @Configuration(proxyBeanMethods = false)
    @ImportRuntimeHints(BookstoreRuntimeHints.class)
    static class Registration {
    }
}
// end::hints[]
