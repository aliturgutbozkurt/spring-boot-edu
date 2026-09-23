package com.springbootedu.setupmodernjava.scopedvalues;

/**
 * Lesson 3.6 — deep in the call stack, the customer is available without being passed as a parameter.
 */
public class CheckoutFlow {

    private final AuditLog log;

    public CheckoutFlow(AuditLog log) {
        this.log = log;
    }

    // tag::read-scoped-value[]
    public void checkout(String isbn) {
        log.add(CurrentCustomer.email() + " bought " + isbn);   // no "customer" parameter needed
    }
    // end::read-scoped-value[]
}
