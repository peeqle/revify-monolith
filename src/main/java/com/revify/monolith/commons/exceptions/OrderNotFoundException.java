package com.revify.monolith.commons.exceptions;

import org.bson.types.ObjectId;

public class OrderNotFoundException extends IllegalArgumentException {
    public OrderNotFoundException(String id) {
        super("Order was not found By ID. ID: " + id);
    }

    public OrderNotFoundException(ObjectId id) {
        super("Order was not found By ID. ID: " + id.toHexString());
    }
}
