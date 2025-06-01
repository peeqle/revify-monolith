package com.revify.monolith.bid.messaging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.bid.models.Auction;
import com.revify.monolith.bid.models.Bid;
import com.revify.monolith.commons.messaging.KafkaTopic;
import com.revify.monolith.commons.models.orders.OrderAdditionalStatus;
import com.revify.monolith.commons.models.orders.OrderCreationDTO;
import com.revify.monolith.commons.models.orders.OrderShipmentParticle;
import com.revify.monolith.commons.models.orders.OrderShipmentStatus;
import com.revify.monolith.geo.model.UserGeolocation;
import com.revify.monolith.geo.service.GeolocationService;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.service.item.ItemReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderProducer {

    private final Gson gson = new GsonBuilder().create();

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ItemReadService itemReadService;

    private final GeolocationService geolocationService;

    public void createOrder(Auction auction, Bid bid) {
        Item item = itemReadService.findById(auction.getItemId());
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found for item: " + auction.getItemId());
        }
        //construct particle
        OrderShipmentParticle.OrderShipmentParticleBuilder builder = OrderShipmentParticle.builder();
        builder.price(bid.getBidPrice());
        builder.courierId(bid.getUserId());
        builder.to(item.getItemDescription().getDestination());

        UserGeolocation latestUserGeolocation = geolocationService.findLatestUserGeolocation(bid.getUserId());
        if (latestUserGeolocation != null) {
            builder.from(latestUserGeolocation.getGeoLocation());
        }
        builder.deliveryTimeEstimated(1000L * 60 * 60 * 24 * 7);

        kafkaTemplate.send(KafkaTopic.ORDER_MODEL_CREATION, gson.toJson(OrderCreationDTO.builder()
                .receivers(List.of(auction.getCreatorId()))
                .items(List.of(auction.getItemId()))
                .deliveryTimeEnd(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli())
                .status(OrderShipmentStatus.CREATED)
                .additionalStatus(OrderAdditionalStatus.CLIENT_PAYMENT_AWAIT)
                .shipmentParticle(builder.build()).build()));
    }
}
