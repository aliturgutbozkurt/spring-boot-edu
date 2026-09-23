package com.springbootedu.setupmodernjava.exercise1;

/**
 * Exercise 1 — given: the kinds of customers.
 */
public sealed interface Customer permits Regular, Student, Member {
}
