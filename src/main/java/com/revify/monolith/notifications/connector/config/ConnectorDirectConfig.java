package com.revify.monolith.notifications.connector.config;

import com.revify.monolith.commons.messaging.RabbitMqTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.revify.monolith.commons.messaging.RabbitMqExchange.DIRECT_EXCHANGE_NOTIFICATIONS;


@Configuration
@RequiredArgsConstructor
public class ConnectorDirectConfig {

    @Bean
    Queue userNotificationQueue() {
        return new Queue(RabbitMqTopic.USER_NOTIFICATIONS, true);
    }

    @Bean
    Queue adminNotificationQueue() {
        return new Queue(RabbitMqTopic.ADMIN_NOTIFICATIONS, true);
    }

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE_NOTIFICATIONS);
    }

    @Bean
    Binding userBinding(DirectExchange exchange) {
        return BindingBuilder.bind(userNotificationQueue()).to(exchange).with("user.notification.#");
    }

    @Bean
    Binding adminBinding(DirectExchange exchange) {
        return BindingBuilder.bind(adminNotificationQueue()).to(exchange).with("admin.notification.#");
    }
}
