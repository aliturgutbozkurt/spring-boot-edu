package com.springbootedu.capstone.order;

import com.springbootedu.capstone.contracts.stock.v1.ReleaseStockRequest;
import com.springbootedu.capstone.contracts.stock.v1.ReleaseStockResponse;
import com.springbootedu.capstone.contracts.stock.v1.ReserveStockRequest;
import com.springbootedu.capstone.contracts.stock.v1.ReserveStockResponse;
import com.springbootedu.capstone.contracts.stock.v1.ReservedLine;
import com.springbootedu.capstone.contracts.stock.v1.StockLine;
import com.springbootedu.capstone.contracts.stock.v1.StockServiceGrpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jspecify.annotations.Nullable;

/**
 * The catalog's StockService, faked in-process: fixed prices, a small stock, and two special ISBNs.
 * It remembers what it was asked, so the tests can check the order service's calls.
 */
public class FakeStockService extends StockServiceGrpc.StockServiceImplBase {

    public static final String EFFECTIVE_JAVA = "9780134685991";        // 45.00, stock 5
    public static final String CLEAN_CODE = "9780132350884";            // 30.50, stock 5
    public static final String SLOW = "0000000000001";                  // answers after one second

    private static final Metadata.Key<String> AUTHORIZATION =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    public final Map<String, List<StockLine>> reserved = new ConcurrentHashMap<>();
    public final List<String> released = new CopyOnWriteArrayList<>();
    public volatile @Nullable String lastAuthorization;

    @Override
    public void reserveStock(ReserveStockRequest request, StreamObserver<ReserveStockResponse> responses) {
        var response = ReserveStockResponse.newBuilder();
        for (StockLine line : request.getLinesList()) {
            switch (line.getIsbn()) {
                case EFFECTIVE_JAVA -> response.addLines(reserved(line, "Effective Java", 4500));
                case CLEAN_CODE -> response.addLines(reserved(line, "Clean Code", 3050));
                case SLOW -> sleepOneSecond();
                default -> {
                    responses.onError(Status.NOT_FOUND.withDescription("unknown book " + line.getIsbn()).asRuntimeException());
                    return;
                }
            }
            if (line.getQuantity() > 5) {
                responses.onError(Status.FAILED_PRECONDITION
                        .withDescription("only 5 left of " + line.getIsbn()).asRuntimeException());
                return;
            }
        }
        reserved.put(request.getOrderRef(), request.getLinesList());
        responses.onNext(response.build());
        responses.onCompleted();
    }

    @Override
    public void releaseStock(ReleaseStockRequest request, StreamObserver<ReleaseStockResponse> responses) {
        released.add(request.getOrderRef());
        responses.onNext(ReleaseStockResponse.newBuilder().setReleased(true).build());
        responses.onCompleted();
    }

    public void reset() {
        reserved.clear();
        released.clear();
        lastAuthorization = null;
    }

    private static ReservedLine reserved(StockLine line, String title, long unitPriceCents) {
        return ReservedLine.newBuilder().setIsbn(line.getIsbn()).setTitle(title)
                .setQuantity(line.getQuantity()).setUnitPriceCents(unitPriceCents).build();
    }

    private static void sleepOneSecond() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Records the Authorization header of every call, to prove that the customer's token is forwarded. */
    public record Recorder(FakeStockService stock) implements ServerInterceptor {

        @Override
        public <Q, R> ServerCall.Listener<Q> interceptCall(ServerCall<Q, R> call, Metadata headers,
                                                          ServerCallHandler<Q, R> next) {
            stock.lastAuthorization = headers.get(AUTHORIZATION);
            return next.startCall(call, headers);
        }
    }
}
