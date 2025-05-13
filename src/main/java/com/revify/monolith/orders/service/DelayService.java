package com.revify.monolith.orders.service;

import com.mongodb.client.result.DeleteResult;
import com.revify.monolith.orders.models.Delay;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DelayService {

    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<Delay> save(Delay delay) {
        return mongoTemplate.save(delay);
    }

    public Mono<DeleteResult> remove(ObjectId id) {
        return mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)));
    }

    public Flux<Delay> findByOrderId(ObjectId id) {
        return mongoTemplate.find(Query.query(Criteria.where("orderId").is(id)), Delay.class);
    }
}
