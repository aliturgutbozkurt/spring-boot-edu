package com.springbootedu.capstone.catalog.stock;

import io.grpc.Status;
import org.springframework.grpc.server.advice.GrpcAdvice;
import org.springframework.grpc.server.advice.GrpcExceptionHandler;

/**
 * Business errors become gRPC status codes the order service can act on (public: see CLAUDE.md, gRPC).
 */
@GrpcAdvice
public class StockErrorAdvice {

    @GrpcExceptionHandler
    public Status unknown(UnknownBookException e) {
        return Status.NOT_FOUND.withDescription(e.getMessage());
    }

    @GrpcExceptionHandler
    public Status insufficient(InsufficientStockException e) {
        return Status.FAILED_PRECONDITION.withDescription(e.getMessage());
    }

    @GrpcExceptionHandler
    public Status busy(IllegalStateException e) {
        return Status.UNAVAILABLE.withDescription(e.getMessage());
    }
}
