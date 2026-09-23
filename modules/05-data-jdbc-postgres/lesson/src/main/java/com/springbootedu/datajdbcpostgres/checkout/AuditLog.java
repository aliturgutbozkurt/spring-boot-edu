package com.springbootedu.datajdbcpostgres.checkout;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lesson 3.5 — every checkout attempt is audited, even when the checkout itself rolls back.
 */
// tag::requires-new[]
@Component
public class AuditLog {

    private final JdbcClient jdbc;

    public AuditLog(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)    // suspend the caller's transaction, commit on its own
    public void record(String message) {
        jdbc.sql("insert into audit_log (message) values (?)").param(message).update();
    }
}
// end::requires-new[]
