package com.springbootedu.capstone.catalog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;

/**
 * The setup of the catalog tests: real MongoDB and Hazelcast, in-process gRPC, a test JWT secret.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(properties = {"bookstore.jwt.secret=" + Tokens.SECRET, "bookstore.catalog.seed=false"})
@AutoConfigureMockMvc
@AutoConfigureTestGrpcTransport
@Import(TestcontainersConfiguration.class)
public @interface CatalogTest {
}
