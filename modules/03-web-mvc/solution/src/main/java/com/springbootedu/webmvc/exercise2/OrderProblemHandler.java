package com.springbootedu.webmvc.exercise2;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exercise 2 — turns the order domain exceptions into RFC 9457 problems.
 */
@RestControllerAdvice
public class OrderProblemHandler {

    @ExceptionHandler
    ProblemDetail outOfStock(OutOfStockException exception, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setType(URI.create("https://springbootedu.com/problems/out-of-stock"));
        problem.setTitle("Out of stock");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("isbn", exception.isbn());
        problem.setProperty("requested", exception.requested());
        problem.setProperty("available", exception.available());
        return problem;
    }

    @ExceptionHandler
    ProblemDetail unknownBook(UnknownBookException exception, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setType(URI.create("https://springbootedu.com/problems/book-not-found"));
        problem.setTitle("Book not found");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("isbn", exception.isbn());
        return problem;
    }
}
