package com.revify.monolith.commons.messaging;

public interface RabbitMqExchange {
    String FANOUT_EXCHANGE_NOTIFICATIONS = "fanout-notification-exchange";
    String DIRECT_EXCHANGE_NOTIFICATIONS = "direct-notification-exchange";
    String TOPIC_EXCHANGE_NOTIFICATIONS = "topic-notification-exchange";
}
