package com.revify.monolith.items.service.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.items.Duration;
import com.revify.monolith.commons.items.ItemUpdatesDTO;
import com.revify.monolith.commons.messaging.dto.FanoutMessageBody;
import com.revify.monolith.commons.models.bid.AuctionChangesRequest;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.model.item.ItemPremium;
import com.revify.monolith.items.model.util.ItemChangesComparator;
import com.revify.monolith.notifications.connector.producers.FanoutNotificationProducer;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static com.revify.monolith.commons.messaging.KafkaTopic.AUCTION_CHANGES;

@Service
public class ItemService {
    private final ReactiveMongoTemplate mongoTemplate;

    private final FanoutNotificationProducer fanoutNotificationProducer;

    private final ItemReadService itemReadService;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    public ItemService(@Qualifier("itemsMongoTemplate") ReactiveMongoTemplate mongoTemplate, FanoutNotificationProducer fanoutNotificationProducer, ItemReadService itemReadService, KafkaTemplate<String, String> kafkaTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.fanoutNotificationProducer = fanoutNotificationProducer;
        this.itemReadService = itemReadService;
        this.kafkaTemplate = kafkaTemplate;
    }

    //todo make payment request to user
    public void attachPremium(Duration duration, ObjectId itemId) {
        itemReadService.findById(itemId)
                .flatMap(existing -> {
                    Long currentUserId = UserUtils.getUserId();
                    if (!existing.getCreatorId().equals(currentUserId)) {
                        return Mono.error(new AccessDeniedException("Access denied"));
                    }

                    ItemPremium newCombined = new ItemPremium();
                    ItemPremium existingPremium = existing.getItemPremium();

                    Instant newDuration = Instant.now().plusMillis(Duration.getDuration(duration).toEpochMilli());
                    //combine premiums???
                    if (existingPremium != null) {

                        //combine two premiums if exists
                        newDuration = newDuration.plusMillis(Instant.ofEpochMilli(existingPremium.getDurationUntil())
                                .minusMillis(Instant.now().toEpochMilli()).toEpochMilli());

                    }
                    newCombined.setDurationUntil(newDuration.toEpochMilli());

                    //expand item's lifetime
                    if (existing.getValidUntil() < newCombined.getDurationUntil()) {
                        existing.setValidUntil(newCombined.getDurationUntil());
                    }

                    return mongoTemplate.save(existing);
                })
                .doOnSuccess((item) ->
                        kafkaTemplate.send(AUCTION_CHANGES,
                                gson.toJson(
                                        AuctionChangesRequest.builder()
                                                .changeValidUntil(item.getValidUntil())
                                                .itemId(item.getId().toHexString())
                                                .build()
                                )
                        )
                ).subscribe();
    }

    /**
     * Changes ItemDescription ONLY
     *
     * @param itemUpdatesDTO
     * @return
     */
    public Mono<Item> updateItem(ItemUpdatesDTO itemUpdatesDTO) {
        return itemReadService.findByIdAndUser(itemUpdatesDTO.itemId(), UserUtils.getUserId())
                .flatMap(existing -> {
                    if (existing.isActive() && !existing.isManuallyToggled()) {
                        return Mono.error(new AccessDeniedException("Access denied"));
                    }
                    if (ItemChangesComparator.compareDto(existing.getItemDescription(), itemUpdatesDTO.description()) == 0) {
                        return Mono.error(new IllegalAccessError("Contents are identical"));
                    }

                    ItemChangesComparator.merge(existing.getItemDescription(), itemUpdatesDTO.description());

                    if ((long) itemUpdatesDTO.validUntil() != existing.getValidUntil()
                            && itemUpdatesDTO.validUntil() > Instant.now()
                            .plus(2, TimeUnit.HOURS.toChronoUnit()).toEpochMilli()) {
                        existing.setValidUntil(itemUpdatesDTO.validUntil());
                    }

                    //todo decide if we can change geoLocation of delivery, if-that so change timespan of changes acceptance

                    return mongoTemplate.save(existing);
                })
                //todo change to normal notifications and topics
                .doOnSuccess((x) ->
                        fanoutNotificationProducer.sendFanout(FanoutMessageBody.builder()
                                .title("Created new item")
                                .body("Notification from item creation")
                                .build())
                );
    }
}
