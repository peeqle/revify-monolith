package com.revify.monolith.notifications.consumers;

import com.revify.monolith.commons.messaging.RabbitMqTopic;
import com.revify.monolith.commons.messaging.dto.DirectMessageBody;
import com.revify.monolith.notifications.service.fcm.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectUserNotificationConsumer {

    private final FcmService fcmService;

    @RabbitListener(queues = RabbitMqTopic.USER_NOTIFICATIONS)
    public void directRabbitListener(DirectMessageBody message) {
        log.debug("Received direct message: {}", message);

        if (message.getDirectNotificationTopic() == null) {
            fcmService.sendDirect(message.getReceiverId(), message.getTitle(), message.getBody());
        } else {
            fcmService.sendDirectToTopic(message.getDirectNotificationTopic().getNotificationTopicName(),
                    message.getReceiverId(), message.getTitle(), message.getBody());
        }
    }
}
