package com.revify.monolith.items.service.composite;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.items.Category;
import com.revify.monolith.commons.messaging.dto.TopicMessageBody;
import com.revify.monolith.currency_reader.service.CurrencyService;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.model.item.composite.CompositeItem;
import com.revify.monolith.items.service.item.ItemReadService;
import com.revify.monolith.notifications.connector.producers.TopicNotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.revify.monolith.items.utils.CompositeItemCriteriaUtil.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class CompositeItemService {

    private final TopicNotificationProducer topicNotificationProducer;

    private final MongoTemplate mongoTemplate;

    private final ItemReadService itemReadService;

    private final CurrencyService currencyService;

    /*
    try to create composite item for item instance and notify all people having same destination items
        about composition excluding sender on client
     */
    public CompositeItem createCompositeInstance(String initialItemId) {
        Item initialItem = itemReadService.findById(initialItemId);
        if (!initialItem.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item is deactivated");
        }

        CompositeItem compositeItem = getCompositeItem(initialItem);
        compositeItem = mongoTemplate.save(compositeItem);

        TopicMessageBody topicMessageBody = new TopicMessageBody();
        topicMessageBody.setSenderId(compositeItem.getCreatorId());
        topicMessageBody.setBody("New composite item created");
        topicMessageBody.setTitle("COMPOSITE ITEM");

        //send notification to couriers waiting for item
//        topicNotificationProducer
//                .sendCompositeNotification(
//                        topicMessageBody,
//                        initialItem.getItemDescription().getDestination().getCountryCode(),
//                        initialItem.getItemDescription().getDestination().getPlaceName()
//                );


        return compositeItem;
    }

    public CompositeItem updateCompositeItem(String compositeItemId, Map<String, Object> updates) {
        CompositeItem compositeItem = findById(compositeItemId);

        if (compositeItem == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Composite item not found");
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            applyUpdate(compositeItem, entry.getKey(), entry.getValue());
        }

        return mongoTemplate.save(compositeItem);
    }

    public void assignItemToComposite(ObjectId compositeId, ObjectId itemId) {
        CompositeItem compositeItem = findById(compositeId.toHexString());
        if (compositeItem == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Composite item not found");
        Item item = itemReadService.findById(itemId.toHexString());
        if (item == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");

        if (compositeItem.getIsActive() && compositeItem.getAuthorizedUsers().contains(UserUtils.getUserId())) {
            compositeItem.getItemsInvolved().add(itemId.toHexString());
            compositeItem.getItemsCategories().addAll(item.getItemDescription().getCategories());

            try {
                compositeItem.setOverallCost(currencyService.mergeTwo(compositeItem.getOverallCost(), item.getPrice()));
            } catch (Exception e) {
                log.warn("Error while merging cost", e);
            }

            compositeItem.getAuthorizedUsers().add(item.getCreatorId());

            mongoTemplate.save(compositeItem);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Composite item cannot be managed");
    }

    public CompositeItem findForItem(String itemId) {
        Query query = Query.query(Criteria.where("initialItemId").is(itemId)
                .and("isActive").is(true));

        return mongoTemplate.findOne(query, CompositeItem.class);
    }

    public Boolean deleteCompositeInstance(String compositeItemId) {
        Query query = Query.query(hasCompositeItemId(compositeItemId));

        return mongoTemplate.remove(query, CompositeItem.class).getDeletedCount() > 0;
    }

    public CompositeItem findById(String compositeItemId) {
        Query query = Query.query(activeCriteria())
                .addCriteria(hasCompositeItemId(compositeItemId));

        return mongoTemplate.findOne(query, CompositeItem.class);
    }

    public Boolean compositeForItemExists(String initialItemId) {
        Query query = Query.query(findForInitialItem(initialItemId))
                .addCriteria(activeCriteria());
        return mongoTemplate.exists(query, CompositeItem.class);
    }

    private void applyUpdate(CompositeItem item, String key, Object value) {
        switch (key) {
            case "availableForAppendix" -> item.setIsAvailableForAppend(Boolean.parseBoolean(value.toString()));
            case "itemsInvolved" -> {
                if (value instanceof List<?> list) {
                    item.setItemsInvolved(new HashSet<>(list.stream()
                            .map(Object::toString)
                            .collect(Collectors.toSet())));
                }
            }
            case "itemsCategories" -> {
                if (value instanceof List<?> list) {
                    item.setItemsCategories(new HashSet<>(list.stream()
                            .map(Object::toString)
                            .map(Category::valueOf)
                            .collect(Collectors.toSet())));
                }
            }
            default -> throw new IllegalArgumentException("Invalid field: " + key);
        }
    }

    private CompositeItem getCompositeItem(Item initialItem) {
        Boolean itemExists = compositeForItemExists(initialItem.getId().toHexString());
        CompositeItem compositeItem = new CompositeItem();
        if (!itemExists) {
            compositeItem.setIsActive(true);
            compositeItem.setCreatorId(UserUtils.getUserId());
            compositeItem.setInitialItemId(initialItem.getId().toHexString());
            compositeItem.setIsAvailableForAppend(true);
            compositeItem.setItemsInvolved(Set.of(initialItem.getId().toHexString()));
            compositeItem.setItemsCategories(initialItem.getItemDescription().getCategories());
            compositeItem.setDestination(initialItem.getItemDescription().getDestination());
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Composite item already exists");
        }
        return compositeItem;
    }
}
