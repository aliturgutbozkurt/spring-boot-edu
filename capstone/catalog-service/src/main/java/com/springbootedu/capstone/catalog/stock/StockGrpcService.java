package com.springbootedu.capstone.catalog.stock;

import com.springbootedu.capstone.contracts.stock.v1.ReleaseStockRequest;
import com.springbootedu.capstone.contracts.stock.v1.ReleaseStockResponse;
import com.springbootedu.capstone.contracts.stock.v1.ReserveStockRequest;
import com.springbootedu.capstone.contracts.stock.v1.ReserveStockResponse;
import com.springbootedu.capstone.contracts.stock.v1.ReservedLine;
import com.springbootedu.capstone.contracts.stock.v1.StockLine;
import com.springbootedu.capstone.contracts.stock.v1.StockServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.grpc.server.service.GrpcService;

/**
 * ADR-2 — the order service reserves stock here, synchronously.
 */
@GrpcService
class StockGrpcService extends StockServiceGrpc.StockServiceImplBase {

    private final StockReservations reservations;

    StockGrpcService(StockReservations reservations) {
        this.reservations = reservations;
    }

    @Override
    public void reserveStock(ReserveStockRequest request, StreamObserver<ReserveStockResponse> responses) {
        Map<String, Integer> quantities = request.getLinesList().stream()
                .collect(Collectors.toMap(StockLine::getIsbn, StockLine::getQuantity, Integer::sum));
        Reservation reservation = reservations.reserve(request.getOrderRef(), quantities);
        ReserveStockResponse.Builder response = ReserveStockResponse.newBuilder();
        reservation.lines().forEach(line -> response.addLines(ReservedLine.newBuilder()
                .setIsbn(line.isbn()).setTitle(line.title()).setQuantity(line.quantity())
                .setUnitPriceCents(cents(line.unitPrice()))));
        responses.onNext(response.build());
        responses.onCompleted();
    }

    @Override
    public void releaseStock(ReleaseStockRequest request, StreamObserver<ReleaseStockResponse> responses) {
        responses.onNext(ReleaseStockResponse.newBuilder().setReleased(reservations.release(request.getOrderRef())).build());
        responses.onCompleted();
    }

    static long cents(BigDecimal price) {
        return price.movePointRight(2).longValueExact();
    }
}
