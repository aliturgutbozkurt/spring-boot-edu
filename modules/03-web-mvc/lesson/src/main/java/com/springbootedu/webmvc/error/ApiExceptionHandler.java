package com.springbootedu.webmvc.error;

import com.springbootedu.webmvc.book.BookNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Lesson 3.3 — one place that turns exceptions into RFC 9457 problem responses (application/problem+json).
 * Extending ResponseEntityExceptionHandler gives ProblemDetails for all Spring MVC exceptions for free.
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    // tag::not-found[]
    @ExceptionHandler
    ProblemDetail handleBookNotFound(BookNotFoundException exception, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setType(URI.create("https://springbootedu.com/problems/book-not-found"));
        problem.setTitle("Book not found");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("bookId", exception.bookId());        // extra, machine-readable field
        return problem;
    }
    // end::not-found[]

    // tag::validation[]
    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, "One or more fields are invalid");
        problem.setTitle("Invalid request");
        List<Map<String, String>> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of("field", error.getField(), "message", String.valueOf(error.getDefaultMessage())))
                .toList();
        problem.setProperty("errors", errors);                   // tell the client WHICH fields are wrong
        return handleExceptionInternal(exception, problem, headers, status, request);
    }
    // end::validation[]
}
