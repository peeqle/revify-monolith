package com.revify.monolith.items.service.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.items.Duration;
import com.revify.monolith.commons.items.ItemUpdatesDTO;
import com.revify.monolith.commons.models.bid.AuctionChangesRequest;
import com.revify.monolith.items.model.ItemEvent;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.model.item.ItemPremium;
import com.revify.monolith.items.model.util.ItemChangesComparator;
import com.revify.monolith.notifications.connector.producers.FanoutNotificationProducer;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static com.revify.monolith.commons.messaging.KafkaTopic.AUCTION_CHANGES;
import static com.revify.monolith.commons.messaging.WsQueues.ITEM_UPDATE;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final MongoTemplate mongoTemplate;

    private final FanoutNotificationProducer fanoutNotificationProducer;

    private final ItemReadService itemReadService;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final SimpMessagingTemplate messagingTemplate;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    //todo make payment request to user
    public Item attachPremium(Duration duration, ObjectId itemId) {
        Item existing = itemReadService.findById(itemId);

        Long currentUserId = UserUtils.getUserId();
        if (!existing.getCreatorId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot modify that item");
        }

        ItemPremium newCombined = new ItemPremium();

        Instant newDuration = Instant.now().plusMillis(Duration.getDuration(duration).toEpochMilli());
        newCombined.setDurationUntil(newDuration.toEpochMilli());

        //expand item's lifetime
        if (existing.getValidUntil() < newCombined.getDurationUntil()) {
            existing.setValidUntil(newCombined.getDurationUntil());
        }

        Item saved = mongoTemplate.save(existing);
        kafkaTemplate.send(AUCTION_CHANGES,
                gson.toJson(
                        AuctionChangesRequest.builder()
                                .changeValidUntil(saved.getValidUntil())
                                .itemId(saved.getId().toHexString())
                                .build()
                )
        );

        return saved;
    }

    /**
     * Changes ItemDescription ONLY
     *
     * @param itemUpdatesDTO
     * @return
     */
    public Item updateItem(ItemUpdatesDTO itemUpdatesDTO) {
        Item existing = itemReadService.findByIdAndUser(itemUpdatesDTO.itemId(), UserUtils.getUserId());
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }

        ItemChangesComparator.merge(existing.getItemDescription(), itemUpdatesDTO.description());

        if ((long) itemUpdatesDTO.validUntil() != existing.getValidUntil()
                && itemUpdatesDTO.validUntil() > Instant.now()
                .plus(2, TimeUnit.HOURS.toChronoUnit()).toEpochMilli()) {
            existing.setValidUntil(itemUpdatesDTO.validUntil());
        }

        //todo decide if we can change geoLocation of delivery, if-that so change timespan of changes acceptance

        existing = mongoTemplate.save(existing);

        messagingTemplate.convertAndSend(ITEM_UPDATE + existing.getId().toHexString(), ItemEvent.builder()
                .activeAt(Instant.now().toEpochMilli())
                .type(ItemEvent.ItemEventType.UPDATE)
                .build());

        return existing;
    }
}
