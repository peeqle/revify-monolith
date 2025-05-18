package com.revify.monolith.items.service.item.changes;

import com.revify.monolith.items.model.item.ItemChangesTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemChangesService {

    private final MongoTemplate mongoTemplate;

    public List<ItemChangesTracker> findChangesForItem(String itemId) {
        return mongoTemplate.find(Query.query(Criteria.where("itemId").is(itemId)), ItemChangesTracker.class);
    }
}
