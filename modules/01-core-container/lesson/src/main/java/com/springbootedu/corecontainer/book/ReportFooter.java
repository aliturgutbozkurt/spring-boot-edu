package com.springbootedu.corecontainer.book;

/**
 * Lesson 3.1 — an optional collaborator: the application works with or without a bean of this type.
 */
@FunctionalInterface
public interface ReportFooter {

    String text();
}
