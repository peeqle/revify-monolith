package com.revify.monolith.notifications.consumers.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revify.monolith.commons.messaging.dto.TopicMessageBody;
import com.revify.monolith.notifications.service.fcm.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.revify.monolith.commons.messaging.RabbitMqTopic.COURIER_NOTIFICATIONS;


@Component
@RequiredArgsConstructor
public class CompositeRequestConsumer {

    private final FcmService fcmService;

    private final ThreadLocal<ObjectMapper> mapperThreadLocal = ThreadLocal.withInitial(ObjectMapper::new);

    @RabbitListener(queues = COURIER_NOTIFICATIONS)
    public void receive(final Message message) {
        System.out.println("Received topic message: " + message);
        if (message.getBody() != null) {
            try {
                TopicMessageBody topicMessageBody = mapperThreadLocal.get().readValue(new String(message.getBody()), TopicMessageBody.class);
                fcmService.sendTopicExcluding(
                        List.of(topicMessageBody.getSenderId()),
                        message.getMessageProperties().getReceivedRoutingKey(),
                        topicMessageBody.getTitle(),
                        topicMessageBody.getBody()
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
