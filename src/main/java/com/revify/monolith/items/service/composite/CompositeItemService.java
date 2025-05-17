package com.revify.monolith.items.service.composite;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.messaging.dto.TopicMessageBody;
import com.revify.monolith.items.model.item.composite.CompositeItem;
import com.revify.monolith.items.service.item.ItemReadService;
import com.revify.monolith.notifications.connector.producers.TopicNotificationProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
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
public class CompositeItemService {

    private final TopicNotificationProducer topicNotificationProducer;

    private final ReactiveMongoTemplate mongoTemplate;

    private final ItemReadService itemReadService;

    @Autowired
    public CompositeItemService(TopicNotificationProducer topicNotificationProducer,
                                ReactiveMongoTemplate mongoTemplate, ItemReadService itemReadService) {
        this.topicNotificationProducer = topicNotificationProducer;
        this.mongoTemplate = mongoTemplate;
        this.itemReadService = itemReadService;
    }

    /*
    try to create composite item for item instance and notify all people having same destination items
        about composition excluding sender on client
     */
    public Mono<CompositeItem> createCompositeInstance(String initialItemId) {
        return itemReadService.findById(initialItemId)
                .flatMap(initialItem -> {
                    if (!initialItem.isActive()) {
                        return Mono.error(new RuntimeException("Item is not active"));
                    }

                    //check if composite for item exist
                    return compositeForItemExists(initialItemId)
                            .onErrorMap(RuntimeException.class, ex -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex))
                            .flatMap(exists -> {
                                if (!exists) {
                                    long userId = UserUtils.getUserId();
                                    CompositeItem compositeItem = new CompositeItem();
                                    compositeItem.setActive(true);
                                    compositeItem.setCreatorId(userId);
                                    compositeItem.setInitialItemId(initialItemId);
                                    compositeItem.setAvailableForAppendix(true);
                                    compositeItem.setItemsInvolved(Set.of(initialItemId));
                                    compositeItem.setItemsCategories(Set.of(initialItem.getItemDescription().getCategory()));
                                    compositeItem.setDestination(initialItem.getItemDescription().getDestination());

                                    return Mono.just(compositeItem);
                                }
                                return Mono.error(new RuntimeException("Composite item already exists"));
                            })
                            .flatMap(mongoTemplate::save)
                            .flatMap(saved -> {
                                TopicMessageBody topicMessageBody = new TopicMessageBody();
                                topicMessageBody.setSenderId(saved.getCreatorId());
                                topicMessageBody.setBody("New composite item created NIGGA");
                                topicMessageBody.setTitle("COMPOSITE ITEM");

                                //send notification to couriers waiting for item
                                topicNotificationProducer
                                        .sendCompositeNotification(
                                                topicMessageBody,
                                                initialItem.getItemDescription().getDestination().getCountryCode(),
                                                initialItem.getItemDescription().getDestination().getPlaceName()
                                        );
                                return Mono.just(saved);
                            });
                });
    }

    public Mono<CompositeItem> updateCompositeItem(String compositeItemId, Map<String, Object> updates) {
        return findById(compositeItemId)
                .flatMap(compositeItem -> {
                    if (compositeItem == null) return Mono.error(new RuntimeException("Cannot find composite item"));
                    for (Map.Entry<String, Object> entry : updates.entrySet()) {
                        applyUpdate(compositeItem, entry.getKey(), entry.getValue());
                    }

                    return mongoTemplate.save(compositeItem);
                });
    }

    public Mono<Boolean> deleteCompositeInstance(String compositeItemId) {
        Query query = Query.query(hasCompositeItemId(compositeItemId));

        return mongoTemplate.remove(query, CompositeItem.class)
                .map(e -> e.getDeletedCount() > 0);
    }

    public Mono<CompositeItem> findById(String compositeItemId) {
        Query query = Query.query(activeCriteria())
                .addCriteria(hasCompositeItemId(compositeItemId));

        return mongoTemplate.findOne(query, CompositeItem.class);
    }

    public Mono<Boolean> compositeForItemExists(String initialItemId) {
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
