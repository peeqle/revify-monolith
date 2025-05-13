package com.revify.monolith.notifications.consumers;

import com.revify.monolith.commons.messaging.NotificationTopics;
import com.revify.monolith.commons.messaging.RabbitMqTopic;
import com.revify.monolith.commons.messaging.dto.FanoutMessageBody;
import com.revify.monolith.notifications.service.fcm.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalNotificationConsumer {

    private final FcmService fcmService;

    @RabbitListener(queues = RabbitMqTopic.GLOBAL_NOTIFICATIONS)
    public void globalRabbitListener(FanoutMessageBody message) {
        log.debug("Received global message");

        try {
            if (message.getNotificationType() == null) {
                fcmService.sendTopic(NotificationTopics.GLOBAL, message.getTitle(), message.getBody());
            } else {
                switch (message.getNotificationType()) {
                    case COURIER ->
                            fcmService.sendTopic(NotificationTopics.COURIERS_GLOBAL, message.getTitle(), message.getBody());
                    case CLIENTS ->
                            fcmService.sendTopic(NotificationTopics.CLIENTS_GLOBAL, message.getTitle(), message.getBody());
                    case MODERATORS ->
                            fcmService.sendTopic(NotificationTopics.MODERATORS_GLOBAL, message.getTitle(), message.getBody());
                    case ADMINISTRATORS ->
                            fcmService.sendTopic(NotificationTopics.ADMINISTRATORS_GLOBAL, message.getTitle(), message.getBody());
                    default -> fcmService.sendTopic(NotificationTopics.GLOBAL, message.getTitle(), message.getBody());
                }
            }
        } catch (NullPointerException e) {
            log.warn("NULL notification caught: {}", message, e);
        }
    }
}
