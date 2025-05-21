package com.revify.monolith.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WsService {

    private final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public static final String ITEM_QUEUE_PREFIX = "item-messages-queue-";
    public static final String BID_QUEUE_PREFIX = "item-messages-queue-";

    public void sendBidCreated(String itemId, String bidId) {
        messagingTemplate.convertAndSend("/topic/item/" + itemId + "/bid/" + bidId, 1);
    }

    public Queue bidCreationQueue(String itemId, String bidId) {
        return new Queue(ITEM_QUEUE_PREFIX + itemId + "-" + BID_QUEUE_PREFIX + bidId, false, true, true);
    }
}
