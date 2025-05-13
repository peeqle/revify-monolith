package com.revify.monolith.notifications.consumers;

import com.revify.monolith.commons.messaging.RabbitMqTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DirectAdminNotificationConsumer {

    @RabbitListener(queues = RabbitMqTopic.ADMIN_NOTIFICATIONS)
    public void directRabbitListener(String message) {
        System.out.println("Received direct admin message: " + message);
    }
}
