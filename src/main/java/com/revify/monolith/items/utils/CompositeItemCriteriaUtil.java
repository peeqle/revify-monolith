package com.revify.monolith.items.utils;

import org.springframework.data.mongodb.core.query.Criteria;

public class CompositeItemCriteriaUtil {
    public static Criteria hasCompositeItemId(String compositeItemId) {
        return Criteria.where("_id").is(compositeItemId);
    }

    public static Criteria filterInitialItem(String itemId) {
        return Criteria.where("initialItemId").ne(itemId);
    }

    public static Criteria findForInitialItem(String itemId) {
        return Criteria.where("initialItemId").is(itemId);
    }

    public static Criteria compositeContainsItemInvolved(String itemId) {
        return Criteria.where("itemsInvolved").is(itemId);
    }

    public static Criteria activeCriteria() {
        return Criteria.where("active").is(true);
    }

    public static Criteria filterNonAvailableComposites() {
        return Criteria.where("availableForAppendix").is(true);
    }
}
