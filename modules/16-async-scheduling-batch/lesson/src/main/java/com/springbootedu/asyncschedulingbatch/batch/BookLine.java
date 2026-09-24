package com.springbootedu.asyncschedulingbatch.batch;

/**
 * Lesson 3.3 — one CSV line, exactly as read: every field is still text.
 */
public record BookLine(String isbn, String title, String price) {
}
