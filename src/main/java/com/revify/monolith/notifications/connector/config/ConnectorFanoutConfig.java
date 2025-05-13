package com.revify.monolith.notifications.connector.config;

import com.revify.monolith.commons.messaging.RabbitMqExchange;
import com.revify.monolith.commons.messaging.RabbitMqTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ConnectorFanoutConfig {

    @Bean
    Queue globalNotificationsQueue() {
        return new Queue(RabbitMqTopic.GLOBAL_NOTIFICATIONS, true);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(RabbitMqExchange.FANOUT_EXCHANGE_NOTIFICATIONS);
    }

    @Bean
    Binding marketingBinding(FanoutExchange exchange) {
        return BindingBuilder.bind(globalNotificationsQueue()).to(exchange);
    }
}
