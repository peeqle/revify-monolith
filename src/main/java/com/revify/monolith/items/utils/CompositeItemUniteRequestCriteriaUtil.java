package com.revify.monolith.items.utils;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;

public class CompositeItemUniteRequestCriteriaUtil {
    public static Criteria hasId(String id) {
        return hasId(new ObjectId(id));
    }

    public static Criteria hasId(ObjectId id) {
        return Criteria.where("_id").is(id);
    }

    public static Criteria containsItemId(String itemId) {
        return Criteria.where("itemId").is(itemId);
    }

    public static Criteria hasCompositeItemId(String compositeItemId) {
        return Criteria.where("compositeItemId").is(compositeItemId);
    }
}
