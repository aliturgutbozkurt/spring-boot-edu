package com.springbootedu.grpc.catalog;

import io.grpc.Status;
import org.springframework.grpc.server.advice.GrpcAdvice;
import org.springframework.grpc.server.advice.GrpcExceptionHandler;

/**
 * Lesson 3.4 — like @ControllerAdvice for REST: Java exceptions become gRPC status codes.
 */
// tag::advice[]
@GrpcAdvice
public class CatalogExceptionAdvice {             // public: Spring gRPC calls the handlers reflectively

    @GrpcExceptionHandler
    public Status notFound(BookNotFoundException e) {
        return Status.NOT_FOUND.withDescription(e.getMessage());
    }

    @GrpcExceptionHandler
    public Status invalid(IllegalArgumentException e) {
        return Status.INVALID_ARGUMENT.withDescription(e.getMessage());
    }
}
// end::advice[]
