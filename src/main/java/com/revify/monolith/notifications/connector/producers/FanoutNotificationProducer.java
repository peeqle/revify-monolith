package com.revify.monolith.notifications.connector.producers;

import com.revify.monolith.commons.messaging.RabbitMqExchange;
import com.revify.monolith.commons.messaging.dto.FanoutMessageBody;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FanoutNotificationProducer {

    private final AmqpTemplate amqpTemplate;

    public void sendFanout(FanoutMessageBody fanoutMessageBody) {
        amqpTemplate.convertAndSend(RabbitMqExchange.FANOUT_EXCHANGE_NOTIFICATIONS, "", fanoutMessageBody);
    }

    public void sendFanout(FanoutMessageBody fanoutMessageBody, String routingKey) {
        amqpTemplate.convertAndSend(RabbitMqExchange.FANOUT_EXCHANGE_NOTIFICATIONS, routingKey, fanoutMessageBody);
    }
}
