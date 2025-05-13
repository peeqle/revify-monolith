package com.revify.monolith.bid.messaging;


import com.revify.monolith.bid.models.Auction;
import com.revify.monolith.bid.models.Bid;
import com.revify.monolith.commons.messaging.KafkaTopic;
import com.revify.monolith.commons.messaging.dto.ItemBillingCreation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void createBill(Bid bidModel, Auction auction) {
        CompletableFuture<SendResult<String, Object>> send = kafkaTemplate.send(KafkaTopic.BILLING_CREATION,
                ItemBillingCreation.builder()
                        .price(bidModel.getBidPrice())
                        .itemId(auction.getItemId())
                        .courierId(bidModel.getUserId())
                        .payerId(auction.getCreatorId())
                        .build()
        );

        send.handleAsync((result, e) -> {
            if (e != null) {
                //notify moderator about action
                log.error("Error creating bill", e);
            }
            return result;
        });
    }
}
