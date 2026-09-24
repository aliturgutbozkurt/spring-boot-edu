package com.springbootedu.reactive.exercise2;

/**
 * Given: the number of copies of a book in stock.
 */
public record StockLevel(String isbn, int quantity) {
}
