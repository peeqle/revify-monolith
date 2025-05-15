package com.revify.monolith.items.service.item;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.items.ItemCreationDTO;
import com.revify.monolith.commons.messaging.dto.FanoutMessageBody;
import com.revify.monolith.commons.models.bid.AuctionCreationRequest;
import com.revify.monolith.commons.models.bid.AuctionToggleRequest;
import com.revify.monolith.commons.models.item.ItemProcessing;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.utils.ItemUtils;
import com.revify.monolith.notifications.connector.producers.FanoutNotificationProducer;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.revify.monolith.commons.messaging.KafkaTopic.*;

@Service
@RequiredArgsConstructor
public class ItemWriteService {

    private final ReactiveMongoTemplate mongoTemplate;

    private final FanoutNotificationProducer fanoutNotificationProducer;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Gson gson = new GsonBuilder().create();

    public Mono<Item> createItem(ItemCreationDTO itemCreationDTO) {
        long userId = UserUtils.getUserId();
        Item newItem = ItemUtils.from(itemCreationDTO);
        newItem.setCreatorId(userId);

        return mongoTemplate.save(newItem)
                .doOnSuccess((x) ->
                        kafkaTemplate.send(AUCTION_CREATION,
                                gson.toJson(
                                        AuctionCreationRequest.defaultBuilder()
                                                .bidsAcceptingTill(x.getValidUntil())
                                                .itemId(x.getId().toHexString())
                                                .userId(userId)
                                                .maximumRequiredBidPrice(x.getItemDescription().getMaximumRequiredBidPrice())
                                                .build()
                                )
                        )
                )
                .doOnSuccess((x) ->
                        {
                            if (!x.getReferenceUrl().isEmpty()) {
                                kafkaTemplate.send(ITEM_PROCESSING_MODEL,
                                        gson.toJson(
                                                ItemProcessing.builder()
                                                        .itemUrl(x.getReferenceUrl())
                                                        .itemId(x.getId().toHexString())
                                                        .build()
                                        )
                                );
                            }
                        }
                )
                .doOnSuccess((x) ->
                        fanoutNotificationProducer.sendFanout(FanoutMessageBody.builder()
                                .title("Created new item")
                                .body("Notification from item creation")
                                .build())
                )
                .doOnSuccess((x) ->
                        fanoutNotificationProducer.sendFanout(FanoutMessageBody.builder()
                                .title("Created new item")
                                .body("Notification from item creation")
                                .build())
                );
    }

    public Mono<Item> deactivateItem(ObjectId itemId, Boolean active) {
        return mongoTemplate.findById(itemId, Item.class)
                .flatMap(existing -> {
                    if (!existing.getCreatorId().equals(UserUtils.getUserId())) {
                        return Mono.error(new AccessDeniedException("You are not allowed to deactivate this item"));
                    }
                    existing.setActive(active);
                    existing.setManuallyToggled(true);

                    return mongoTemplate.save(existing);
                })
                .doOnSuccess((x) ->
                        kafkaTemplate.send(AUCTION_DEACTIVATION,
                                gson.toJson(
                                        AuctionToggleRequest.builder()
                                                .manuallyToggled(true)
                                                .status(active)
                                                .itemId(x.getId().toHexString())
                                                .build()
                                )
                        )
                );

        //send notification to users subscribed
    }
}
