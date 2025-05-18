package com.revify.monolith.items.service.composite;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.messaging.dto.TopicMessageBody;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.model.item.composite.CompositeItem;
import com.revify.monolith.items.service.item.ItemReadService;
import com.revify.monolith.notifications.connector.producers.TopicNotificationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.revify.monolith.items.utils.CompositeItemCriteriaUtil.*;


@Service
@RequiredArgsConstructor
public class CompositeItemService {

    private final TopicNotificationProducer topicNotificationProducer;

    private final MongoTemplate mongoTemplate;

    private final ItemReadService itemReadService;

    /*
    try to create composite item for item instance and notify all people having same destination items
        about composition excluding sender on client
     */
    public CompositeItem createCompositeInstance(String initialItemId) {
        Item initialItem = itemReadService.findById(initialItemId);
        if (!initialItem.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item is deactivated");
        }
        Boolean itemExists = compositeForItemExists(initialItemId);

        CompositeItem compositeItem = new CompositeItem();
        if (!itemExists) {
            compositeItem.setActive(true);
            compositeItem.setCreatorId(UserUtils.getUserId());
            compositeItem.setInitialItemId(initialItemId);
            compositeItem.setAvailableForAppendix(true);
            compositeItem.setItemsInvolved(Set.of(initialItemId));
            compositeItem.setItemsCategories(initialItem.getItemDescription().getCategories());
            compositeItem.setDestination(initialItem.getItemDescription().getDestination());
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Composite item already exists");
        }
        compositeItem = mongoTemplate.save(compositeItem);

        TopicMessageBody topicMessageBody = new TopicMessageBody();
        topicMessageBody.setSenderId(compositeItem.getCreatorId());
        topicMessageBody.setBody("New composite item created");
        topicMessageBody.setTitle("COMPOSITE ITEM");

        //send notification to couriers waiting for item
        topicNotificationProducer
                .sendCompositeNotification(
                        topicMessageBody,
                        initialItem.getItemDescription().getDestination().getCountryCode(),
                        initialItem.getItemDescription().getDestination().getPlaceName()
                );


        return compositeItem;
    }

    public CompositeItem updateCompositeItem(String compositeItemId, Map<String, Object> updates) {
        CompositeItem byId = findById(compositeItemId);

        if (byId == null) return Mono.error(new RuntimeException("Cannot find composite item"));
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            applyUpdate(compositeItem, entry.getKey(), entry.getValue());
        }

        return mongoTemplate.save(byId);
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

    /*
    find all elements that could be used for composition
     */

    public Flux<CompositeItem> findCompositeItemsContaining(String itemId) {
        Query query = new Query();
        query.addCriteria(compositeContainsItemInvolved(itemId));
        query.addCriteria(filterInitialItem(itemId));
        query.addCriteria(filterNonAvailableComposites());

        return mongoTemplate.find(query, CompositeItem.class);
    }

    private void applyUpdate(CompositeItem item, String key, Object value) {
        switch (key) {
            case "availableForAppendix" -> item.setAvailableForAppendix(Boolean.parseBoolean(value.toString()));
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
                            .collect(Collectors.toSet())));
                }
            }
            default -> throw new IllegalArgumentException("Invalid field: " + key);
        }
    }
}
