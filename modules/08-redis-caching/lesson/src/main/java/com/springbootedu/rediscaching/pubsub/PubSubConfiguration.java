package com.springbootedu.rediscaching.pubsub;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Lesson 3.6 — the container keeps a subscription open and dispatches messages to listeners.
 */
// tag::subscribe[]
@Configuration(proxyBeanMethods = false)
public class PubSubConfiguration {

    @Bean
    RedisMessageListenerContainer priceChangeSubscription(RedisConnectionFactory connections,
                                                          PriceChangeListener listener) {
        var container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connections);
        container.addMessageListener(listener, new ChannelTopic(PriceChangePublisher.CHANNEL));   // SUBSCRIBE
        return container;
    }
}
// end::subscribe[]
