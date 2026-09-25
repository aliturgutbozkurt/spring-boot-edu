package com.springbootedu.grpc.exercise3;

import com.springbootedu.grpc.exercises.v1.BookServiceGrpc;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ImportGrpcClients;

/**
 * Given — the blocking stub for the channel "books".
 */
@Configuration(proxyBeanMethods = false)
@ImportGrpcClients(target = "books", types = BookServiceGrpc.BookServiceBlockingStub.class)
class ClientConfiguration {
}
