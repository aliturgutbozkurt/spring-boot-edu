package com.springbootedu.capstone.order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.service.GrpcService;

/**
 * The setup of the order tests: real PostgreSQL, the fake StockService in-process, a test JWT secret
 * and a short catalog deadline.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(properties = {"bookstore.jwt.secret=" + Tokens.SECRET, "bookstore.catalog.deadline=500ms"})
@AutoConfigureMockMvc
@AutoConfigureTestGrpcTransport
@Import({TestcontainersConfiguration.class, OrderTest.FakeCatalog.class})
public @interface OrderTest {

    @TestConfiguration(proxyBeanMethods = false)
    class FakeCatalog {

        public static final FakeStockService STOCK = new FakeStockService();

        @Bean
        @GrpcService
        FakeStockService fakeStockService() {
            return STOCK;
        }

        @Bean
        @GlobalServerInterceptor
        FakeStockService.Recorder recordHeaders() {
            return new FakeStockService.Recorder(STOCK);
        }
    }
}
