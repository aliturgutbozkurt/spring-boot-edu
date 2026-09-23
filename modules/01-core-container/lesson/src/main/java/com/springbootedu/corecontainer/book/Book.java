package com.springbootedu.corecontainer.book;

import java.math.BigDecimal;

/**
 * The Bookstore domain object used throughout the course.
 */
public record Book(String isbn, String title, String author, BigDecimal price) {
}
