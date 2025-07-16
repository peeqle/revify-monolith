package com.revify.monolith.items.service.item;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.revify.monolith.bid.service.AuctionService;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.items.ItemCreationDTO;
import com.revify.monolith.commons.models.bid.AuctionCreationRequest;
import com.revify.monolith.commons.models.bid.AuctionToggleRequest;
import com.revify.monolith.commons.models.item.ItemProcessing;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.service.composite.CompositeItemService;
import com.revify.monolith.items.utils.ItemUtils;
import com.revify.monolith.shoplift.model.Shoplift;
import com.revify.monolith.shoplift.service.ShopliftService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static com.revify.monolith.commons.messaging.KafkaTopic.ITEM_ADD_SHOPLIFT;
import static com.revify.monolith.commons.messaging.KafkaTopic.ITEM_PROCESSING_MODEL;

@Service
@RequiredArgsConstructor
public class ItemWriteService {

    private final MongoTemplate mongoTemplate;

    private final AuctionService auctionService;

    private final CompositeItemService compositeItemService;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ShopliftService shopliftService;

    private final Gson gson = new GsonBuilder().create();

    public Item createItem(ItemCreationDTO itemCreationDTO) {
        Item newItem;
        if (itemCreationDTO.shoplift().getShopliftId() != null) {
            Shoplift shoplift = shopliftService.getById(itemCreationDTO.shoplift().getShopliftId());
            newItem = ItemUtils.from(itemCreationDTO, shoplift);
            newItem.setShopliftId(shoplift.getId().toHexString());
        } else {
            newItem = ItemUtils.from(itemCreationDTO);
        }
        newItem.setCreatorId(UserUtils.getUserId());

        newItem = mongoTemplate.save(newItem);
        if (newItem.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item creation failed");
        }

        if (itemCreationDTO.shoplift().getShopliftId() == null || itemCreationDTO.shoplift().getShopliftId().isBlank()) {
            //create auction
            toggleAuctionCreateForItem(newItem);

            //create composite item
            if (newItem.getItemDescription().getCompositeStackingEnabled()) {
                compositeItemService.createCompositeInstance(newItem.getId().toHexString());
            }

            //send item for deeper processing
            if (!newItem.getReferenceUrl().isEmpty()) {
                kafkaTemplate.send(ITEM_PROCESSING_MODEL,
                        gson.toJson(
                                ItemProcessing.builder()
                                        .itemUrl(newItem.getReferenceUrl())
                                        .itemId(newItem.getId().toHexString())
                                        .build()
                        )
                );
            }

            //append item to the shoplifting system, queue for optimized load
            if (itemCreationDTO.shoplift().getAddToShoplift()) {
                kafkaTemplate.send(ITEM_ADD_SHOPLIFT, newItem.getId().toHexString());
            }
        } else {
            shopliftService.addItem(newItem);
        }

        return newItem;
    }

    public Item deactivateItem(ObjectId itemId, Boolean active) {
        Item item = mongoTemplate.findById(itemId, Item.class);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
        if (!item.getCreatorId().equals(UserUtils.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot modify this item");
        }

        item.setActive(active);
        item.setManuallyToggled(true);

        item = mongoTemplate.save(item);

        //auction deactivation
        auctionService.toggleAuctionStatus(AuctionToggleRequest.builder()
                .manuallyToggled(true)
                .status(active)
                .itemId(item.getId().toHexString())
                .build());

        return item;
    }

    public void toggleAuctionCreateForItem(Item newItem) {
        auctionService.createAuction(AuctionCreationRequest.defaultBuilder()
                .bidsAcceptingTill(newItem.getValidUntil())
                .itemId(newItem.getId().toHexString())
                .userId(UserUtils.getUserId())
                .maximumRequiredBidPrice(newItem.getItemDescription().getMaximumRequiredBidPrice())
                .build());
    }
}
