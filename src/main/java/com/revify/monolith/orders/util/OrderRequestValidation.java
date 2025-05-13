package com.revify.monolith.orders.util;

import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

public class OrderRequestValidation {
    public static Mono<ObjectId> validateOrderId(String orderId) {
        if (!ObjectId.isValid(orderId)) {
            return Mono.error(new IllegalArgumentException("Invalid orderId format."));
        }
        return Mono.just(new ObjectId(orderId));
    }
}
