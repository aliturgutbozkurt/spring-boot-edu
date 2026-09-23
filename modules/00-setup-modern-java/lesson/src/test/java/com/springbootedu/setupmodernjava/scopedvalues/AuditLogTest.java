package com.springbootedu.setupmodernjava.scopedvalues;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Lesson 3.6 — scoped values carry context without method parameters.
 */
class AuditLogTest {

    @Test
    void readsTheCustomerBoundForTheCurrentScope() {
        var log = new AuditLog();

        CurrentCustomer.runAs("ayse@example.com", () -> new CheckoutFlow(log).checkout("978-0-13-468599-1"));

        assertThat(log.entries()).containsExactly("ayse@example.com bought 978-0-13-468599-1");
    }

    @Test
    void outsideAScopeTheCustomerIsAnonymous() {
        var log = new AuditLog();

        new CheckoutFlow(log).checkout("978-0-13-468599-1");

        assertThat(log.entries()).containsExactly("anonymous bought 978-0-13-468599-1");
    }
}
