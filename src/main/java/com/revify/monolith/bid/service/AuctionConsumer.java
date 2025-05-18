package com.revify.monolith.bid.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.commons.messaging.ConsumerGroups;
import com.revify.monolith.commons.messaging.KafkaTopic;
import com.revify.monolith.commons.models.bid.AuctionChangesRequest;
import com.revify.monolith.commons.models.bid.AuctionCreationRequest;
import com.revify.monolith.commons.models.bid.AuctionToggleRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionConsumer {

    private final AuctionService auctionService;

    private final Gson gson = new GsonBuilder().create();

    @KafkaListener(topics = KafkaTopic.AUCTION_CREATION, groupId = ConsumerGroups.BIDS)
    public void listenAuctionCreation(@Payload String message) {
        System.out.println("Received Message: " + message);

        AuctionCreationRequest auctionCreationRequest = gson.fromJson(message, AuctionCreationRequest.class);
        if (message != null && !message.isEmpty() && auctionCreationRequest != null) {
            auctionService.createAuction(auctionCreationRequest);
        }
    }

    @KafkaListener(topics = KafkaTopic.AUCTION_DEACTIVATION, groupId = ConsumerGroups.BIDS)
    public void listenAuctionDeactivation(@Payload String message) {
        System.out.println("Received Message: " + message);

        AuctionToggleRequest auctionToggleRequest = gson.fromJson(message, AuctionToggleRequest.class);
        if (message != null && !message.isEmpty() && auctionToggleRequest != null) {
            auctionService.toggleAuctionStatus(auctionToggleRequest);
        }
    }


    //todo handle errors with auction
    @KafkaListener(topics = KafkaTopic.AUCTION_CHANGES, groupId = ConsumerGroups.BIDS)
    public void listenAuctionChanges(@Payload String message) {
        System.out.println("Received Message: " + message);

        AuctionChangesRequest auctionChangesRequest = gson.fromJson(message, AuctionChangesRequest.class);
        if (message != null && !message.isEmpty() && auctionChangesRequest != null) {
            auctionService.changeAuction(auctionChangesRequest);
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
