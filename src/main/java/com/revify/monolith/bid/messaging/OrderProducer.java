package com.revify.monolith.bid.messaging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.bid.models.Auction;
import com.revify.monolith.bid.models.Bid;
import com.revify.monolith.commons.messaging.KafkaTopic;
import com.revify.monolith.commons.models.orders.OrderCreationDTO;
import com.revify.monolith.commons.models.orders.OrderShipmentParticle;
import com.revify.monolith.commons.models.orders.OrderShipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class OrderProducer {

    private final Gson gson = new GsonBuilder().create();

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void createOrder(Auction auction, Bid bid) {
        kafkaTemplate.send(KafkaTopic.ORDER_MODEL_CREATION, gson.toJson(OrderCreationDTO.builder()
                .receiverId(auction.getCreatorId())
                .itemId(auction.getItemId())
                .deliveryTimeEnd(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli())
                .status(OrderShipmentStatus.CREATED)
                .shipmentParticle(
                        OrderShipmentParticle.builder()
                                .courierId(bid.getUserId())
                                //todo to-from and time estimation
                                .price(bid.getBidPrice())
                                .build()
                ).build()));
    }
}
