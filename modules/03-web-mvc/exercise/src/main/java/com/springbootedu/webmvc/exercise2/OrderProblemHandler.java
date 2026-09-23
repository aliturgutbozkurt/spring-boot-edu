package com.springbootedu.webmvc.exercise2;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exercise 2 — turns the order domain exceptions into RFC 9457 problems.
 */
@RestControllerAdvice
public class OrderProblemHandler {

    // TODO 2a: OutOfStockException → 409 CONFLICT problem
    //          type     https://springbootedu.com/problems/out-of-stock
    //          title    Out of stock
    //          detail   the exception message
    //          instance the request path
    //          extra properties: isbn, requested, available

    // TODO 2b: UnknownBookException → 404 NOT FOUND problem
    //          type https://springbootedu.com/problems/book-not-found, title "Book not found",
    //          instance the request path, extra property: isbn
}
