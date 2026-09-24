package com.springbootedu.graphqlwebsocket.notify;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Lesson 3.6 — STOMP over WebSocket with the in-memory "simple broker".
 */
// tag::stomp-config[]
@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws");                               // clients connect to ws://host:8080/ws
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry broker) {
        broker.enableSimpleBroker("/topic");                        // server → clients: /topic/...
        broker.setApplicationDestinationPrefixes("/app");           // clients → @MessageMapping methods: /app/...
    }
}
// end::stomp-config[]
