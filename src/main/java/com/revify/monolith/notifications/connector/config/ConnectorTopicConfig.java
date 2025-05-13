package com.revify.monolith.notifications.connector.config;

import com.revify.monolith.commons.messaging.ExchangeBindings;
import com.revify.monolith.commons.messaging.RabbitMqTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.revify.monolith.commons.messaging.RabbitMqExchange.TOPIC_EXCHANGE_NOTIFICATIONS;


@Configuration
@RequiredArgsConstructor
public class ConnectorTopicConfig {

    @Bean
    Queue courierNotificationQueue() {
        return new Queue(RabbitMqTopic.COURIER_NOTIFICATIONS, true);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NOTIFICATIONS);
    }

    @Bean
    Binding topicBinding(TopicExchange exchange) {
        return BindingBuilder.bind(courierNotificationQueue()).to(exchange).with("user.notification.#");
    }

    @Bean
    Binding courierNotificationCompositeBindingBinding(TopicExchange exchange) {
        return BindingBuilder.bind(courierNotificationQueue()).to(exchange).with(ExchangeBindings.CLIENT_COMPOSITE + "#");
    }
}
