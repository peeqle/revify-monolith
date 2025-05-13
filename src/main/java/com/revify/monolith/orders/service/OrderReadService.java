package com.revify.monolith.orders.service;

import com.revify.monolith.commons.exceptions.OrderNotFoundException;
import com.revify.monolith.orders.models.Order;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderReadService {
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<Order> findOrderById(String orderId) {
        return findOrderById(new ObjectId(orderId));
    }

    public Mono<Order> findOrderById(ObjectId orderId) {
        return mongoTemplate.findById(orderId, Order.class)
                .switchIfEmpty(Mono.error(new OrderNotFoundException(orderId)));
    }
}
