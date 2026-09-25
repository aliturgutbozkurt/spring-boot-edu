package com.springbootedu.grpc.catalog;

import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.5 — a server interceptor wraps every call, like a servlet filter; it logs method, status and time.
 */
// tag::interceptor[]
@Component
@GlobalServerInterceptor                                   // applies to all gRPC services of this server
@Order(Ordered.HIGHEST_PRECEDENCE)                         // outermost: it also sees calls closed by the error mapping
public class CallLoggingInterceptor implements ServerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CallLoggingInterceptor.class);

    private final List<String> recentCalls = new CopyOnWriteArrayList<>();

    @Override
    public <Q, R> ServerCall.Listener<Q> interceptCall(ServerCall<Q, R> call, Metadata headers,
                                                       ServerCallHandler<Q, R> next) {
        long start = System.nanoTime();
        String method = call.getMethodDescriptor().getFullMethodName();
        ServerCall<Q, R> timedCall = new SimpleForwardingServerCall<>(call) {
            @Override
            public void close(Status status, Metadata trailers) {   // called once, when the call ends
                log.info("{} {} in {} ms", method, status.getCode(), (System.nanoTime() - start) / 1_000_000);
                recentCalls.add(method + " " + status.getCode());
                super.close(status, trailers);
            }
        };
        return next.startCall(timedCall, headers);
    }
    // end::interceptor[]

    public List<String> recentCalls() {
        return List.copyOf(recentCalls);
    }

    public void clear() {
        recentCalls.clear();
    }
}
