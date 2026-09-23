package com.springbootedu.setupmodernjava.scopedvalues;

/**
 * Lesson 3.6 — a {@link ScopedValue} is an immutable, bounded alternative to ThreadLocal.
 */
// tag::scoped-value[]
public final class CurrentCustomer {

    private static final ScopedValue<String> EMAIL = ScopedValue.newInstance();

    private CurrentCustomer() {
    }

    public static void runAs(String email, Runnable action) {
        ScopedValue.where(EMAIL, email).run(action);     // bound only while action runs
    }

    public static String email() {
        return EMAIL.orElse("anonymous");                // unbound outside the scope
    }
}
// end::scoped-value[]
