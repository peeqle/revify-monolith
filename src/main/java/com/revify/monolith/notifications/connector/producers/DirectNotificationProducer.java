package com.revify.monolith.notifications.connector.producers;

import com.revify.monolith.commons.messaging.RabbitMqExchange;
import com.revify.monolith.commons.messaging.dto.DirectMessageBody;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DirectNotificationProducer {

    private final AmqpTemplate amqpTemplate;

    public void sendDirect(String directKey, DirectMessageBody messageBody) {
        amqpTemplate.convertAndSend(RabbitMqExchange.DIRECT_EXCHANGE_NOTIFICATIONS, directKey, messageBody);
    }
}
