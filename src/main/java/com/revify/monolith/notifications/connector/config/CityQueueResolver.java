package com.revify.monolith.notifications.connector.config;

import com.revify.monolith.commons.messaging.ExchangeBindings;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@RequiredArgsConstructor
public class CityQueueResolver {

    private final AmqpAdmin amqpAdmin;

    private final TopicExchange topicExchange;

    public boolean queueExists(String queueName) {
        try {
            Properties properties = amqpAdmin.getQueueProperties(queueName);
            return properties != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void createCityQueue(String city) {
        if (queueExists(city)) {
            return;
        }

        String queueName = "queue-" + city.toLowerCase();
        Queue queue = new Queue(queueName, true);
        amqpAdmin.declareQueue(queue);

        Binding binding = BindingBuilder.bind(queue)
                .to(topicExchange)
                .with(ExchangeBindings.CLIENT_COMPOSITE + city.toLowerCase() + ".#");
        amqpAdmin.declareBinding(binding);
    }
}
