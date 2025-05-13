package com.revify.monolith.bid.service;


import com.revify.monolith.commons.messaging.ConsumerGroups;
import com.revify.monolith.commons.messaging.KafkaTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

@Service
@ApplicationScope
@RequiredArgsConstructor
public class ItemStatusHolder {

    @KafkaListener(topics = KafkaTopic.ITEM_STATUS_TRACKER, groupId = ConsumerGroups.AUCTION_STATUS)
    public void listenItemStatusChanges(@Payload String message) {

    }
}
