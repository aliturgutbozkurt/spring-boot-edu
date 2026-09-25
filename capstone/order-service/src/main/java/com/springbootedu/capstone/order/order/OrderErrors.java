package com.springbootedu.capstone.order.order;

import com.springbootedu.capstone.order.stock.CatalogUnavailableException;
import com.springbootedu.capstone.order.stock.OutOfStockException;
import com.springbootedu.capstone.order.stock.UnknownBookException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Errors as RFC 9457 ProblemDetail. The catalog's gRPC status codes become HTTP status codes here.
 */
@RestControllerAdvice
class OrderErrors {

    @ExceptionHandler
    ProblemDetail outOfStock(OutOfStockException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler
    ProblemDetail unknownBook(UnknownBookException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, e.getMessage());
    }

    @ExceptionHandler
    ProblemDetail catalogUnavailable(CatalogUnavailableException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler
    ProblemDetail notFound(OrderNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
