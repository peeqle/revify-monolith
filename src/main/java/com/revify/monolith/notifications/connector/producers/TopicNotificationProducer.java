package com.revify.monolith.notifications.connector.producers;

import com.revify.monolith.commons.messaging.ExchangeBindings;
import com.revify.monolith.commons.messaging.dto.TopicMessageBody;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import static com.revify.monolith.commons.messaging.RabbitMqExchange.TOPIC_EXCHANGE_NOTIFICATIONS;


@Component
@RequiredArgsConstructor
public class TopicNotificationProducer {

    private final AmqpTemplate amqpTemplate;

    public void sendCompositeNotification(TopicMessageBody topicMessageBody, String destinationCountry, String destinationCity) {
        String routingKey = ExchangeBindings.CLIENT_COMPOSITE + destinationCountry.toLowerCase() + "." + destinationCity.toLowerCase() + ".#";

        amqpTemplate.convertAndSend(TOPIC_EXCHANGE_NOTIFICATIONS, routingKey, topicMessageBody);
    }
}
