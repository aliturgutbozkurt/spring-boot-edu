package com.springbootedu.messagingkafka.invoicing;

import com.springbootedu.messagingkafka.order.OrderEvents;
import com.springbootedu.messagingkafka.order.OrderPlaced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.4 — the invoicing service is sometimes down. Failed orders wait in retry topics instead of blocking.
 */
// tag::retryable-topic[]
@Component
class InvoiceListener {

    private static final Logger log = LoggerFactory.getLogger(InvoiceListener.class);

    private final Invoices invoices;

    InvoiceListener(Invoices invoices) {
        this.invoices = invoices;
    }

    @RetryableTopic(attempts = "3",                                     // 1 try + 2 retries
            backOff = @BackOff(delay = 500, multiplier = 2),            // 500 ms, then 1 s
            retryTopicSuffix = "-invoicing-retry",                     // orders-invoicing-retry-500, -1000
            dltTopicSuffix = "-invoicing-dlt",                         // orders-invoicing-dlt
            sameIntervalTopicReuseStrategy = SameIntervalTopicReuseStrategy.MULTIPLE_TOPICS)
    @KafkaListener(topics = OrderEvents.TOPIC, groupId = "invoicing")
    void onOrderPlaced(OrderPlaced order) {
        int attempt = invoices.countAttempt(order.orderId());
        if (order.customerId().startsWith("flaky-") && attempt < 3) {
            throw new IllegalStateException("invoicing service unavailable (attempt " + attempt + ")");
        }
        invoices.invoice(order.orderId());
        log.info("Invoiced order {} after {} attempt(s)", order.orderId(), attempt);
    }

    @DltHandler
    void onGiveUp(OrderPlaced order) {
        log.warn("Invoicing gave up on order {}", order.orderId());
    }
}
// end::retryable-topic[]
