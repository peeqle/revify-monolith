package com.revify.monolith.items.service.item.changes;

import com.revify.monolith.items.model.item.ItemChangesTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ItemChangesService {

    private final ReactiveMongoTemplate mongoTemplate;

    @Autowired
    public ItemChangesService(@Qualifier("itemsMongoTemplate") ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Flux<ItemChangesTracker> findChangesForItem(String itemId) {
        return mongoTemplate.find(Query.query(Criteria.where("itemId").is(itemId)), ItemChangesTracker.class);
    }
}
