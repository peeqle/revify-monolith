package com.revify.monolith.orders.service;

import com.mongodb.client.result.DeleteResult;
import com.revify.monolith.orders.models.Delay;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DelayService {

    private final MongoTemplate mongoTemplate;

    public Delay save(Delay delay) {
        return mongoTemplate.save(delay);
    }

    public DeleteResult remove(ObjectId id) {
        return mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)));
    }

    public Delay findByOrderId(ObjectId id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("orderId").is(id))
                .with(Sort.by(Sort.Direction.DESC, "createdAt")), Delay.class);
    }
}
