package com.revify.monolith.bid.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.commons.messaging.ConsumerGroups;
import com.revify.monolith.commons.messaging.KafkaTopic;
import com.revify.monolith.commons.models.bid.AuctionChangesRequest;
import com.revify.monolith.commons.models.bid.AuctionCreationRequest;
import com.revify.monolith.items.model.ItemEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.revify.monolith.RabbitQueues.ITEM_UPDATE;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionConsumer {

    private final AuctionService auctionService;

    private final SimpMessagingTemplate messagingTemplate;

    private final Gson gson = new GsonBuilder().create();


    //todo handle errors with auction
    @KafkaListener(topics = KafkaTopic.AUCTION_CHANGES, groupId = ConsumerGroups.BIDS)
    public void listenAuctionChanges(@Payload String message) {
        System.out.println("Received Message: " + message);

        AuctionChangesRequest auctionChangesRequest = gson.fromJson(message, AuctionChangesRequest.class);
        if (message != null && !message.isEmpty() && auctionChangesRequest != null) {
            auctionService.changeAuction(auctionChangesRequest);

            messagingTemplate.convertAndSend(ITEM_UPDATE + auctionChangesRequest.getItemId(), ItemEvent.builder()
                    .activeAt(Instant.now().toEpochMilli())
                    .type(ItemEvent.ItemEventType.UPDATE)
                    .build());
        }
    }

    @KafkaListener(topics = KafkaTopic.AUCTION_RECREATION_EXPLICIT, groupId = ConsumerGroups.BIDS_HIGHEST)
    public void listenAuctionRecreation(@Payload String message) {
        System.out.println("Received Message: " + message);

        AuctionCreationRequest auctionCreationRequest = gson.fromJson(message, AuctionCreationRequest.class);
        if (message != null) {
            auctionService.recreateAuction(auctionCreationRequest);
        }
    }
}
