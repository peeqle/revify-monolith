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
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionConsumer {

    private final AuctionService auctionService;

    private final Gson gson = new GsonBuilder().create();

    @KafkaListener(topics = KafkaTopic.AUCTION_CREATION, groupId = ConsumerGroups.BIDS)
    public void listenAuctionCreation(@Payload String message) {
        System.out.println("Received Message: " + message);

        if (message != null) {
            auctionService.createAuction(Mono.just(gson.fromJson(message, AuctionCreationRequest.class)))
                    .subscribe(e -> log.info("Saved new Auction {}", e.getId()),
                            ex -> log.error("Cannot save Auction", ex),
                            () -> log.info("Completed Auction save")
                    );
        }
    }

    @KafkaListener(topics = KafkaTopic.AUCTION_DEACTIVATION, groupId = ConsumerGroups.BIDS)
    public void listenAuctionDeactivation(@Payload String message) {
        System.out.println("Received Message: " + message);

        if (message != null) {
            auctionService.deactivateAuction(gson.fromJson(message, AuctionToggleRequest.class))
                    .subscribe(e -> log.info("Deactivated auction {}", e.getId()),
                            ex -> log.error("Cannot deactivate auction.", ex),
                            () -> log.info("Completed auction deactivation.")
                    );
        }
    }


    //todo handle errors with auction
    @KafkaListener(topics = KafkaTopic.AUCTION_CHANGES, groupId = ConsumerGroups.BIDS)
    public void listenAuctionChanges(@Payload String message) {
        System.out.println("Received Message: " + message);

        if (message != null) {
            auctionService.changeAuction(gson.fromJson(message, AuctionChangesRequest.class))
                    .subscribe(e -> log.info("Changed auction {}", e.getId()),
                            ex -> log.error("Cannot change auction.", ex),
                            () -> log.info("Completed auction deactivation.")
                    );
        }
    }

    @KafkaListener(topics = KafkaTopic.AUCTION_RECREATION_EXPLICIT, groupId = ConsumerGroups.BIDS_HIGHEST)
    public void listenAuctionRecreation(@Payload String message) {
        System.out.println("Received Message: " + message);

        if (message != null) {
            auctionService.deactivateAuction(gson.fromJson(message, AuctionToggleRequest.class))
                    .subscribe(e -> log.info("Deactivated auction {}", e.getId()),
                            ex -> log.error("Cannot deactivate auction.", ex),
                            () -> log.info("Completed auction deactivation.")
                    );
        }
    }
}
