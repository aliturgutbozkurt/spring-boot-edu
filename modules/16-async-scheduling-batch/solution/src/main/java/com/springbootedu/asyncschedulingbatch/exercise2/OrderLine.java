package com.springbootedu.asyncschedulingbatch.exercise2;

/**
 * Given: one CSV line of the order file.
 */
public record OrderLine(String orderId, String isbn, String quantity) {
}
