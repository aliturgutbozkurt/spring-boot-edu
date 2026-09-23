package com.springbootedu.datajdbcpostgres.exercise3;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Exercise 3 — writes the outcome of an import in its own transaction.
 */
@Component
public class ImportLog {

    private final JdbcClient jdbc;

    public ImportLog(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String isbn, String status, String detail) {
        jdbc.sql("insert into import_log (isbn, status, detail) values (?, ?, ?)")
                .params(isbn, status, detail)
                .update();
    }
}
