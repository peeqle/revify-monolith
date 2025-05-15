package com.revify.monolith.items.service.composite;

import com.revify.monolith.items.model.item.composite.CompositeItemUniteRequest;
import com.revify.monolith.items.utils.CompositeItemUniteRequestCriteriaUtil;
import io.vavr.Tuple;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class CompositeItemUniteRequestService {

    private final ReactiveMongoTemplate mongoTemplate;

    public CompositeItemUniteRequestService(@Qualifier("itemsMongoTemplate") ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Mono<CompositeItemUniteRequest> createComposeItemRequest(String itemId, String compositeItemId) {
        return Mono.just(Tuple.of(itemId, compositeItemId))
                .flatMap(tuple -> {
                    Query query = Query.query(CompositeItemUniteRequestCriteriaUtil.containsItemId(tuple._1))
                            .addCriteria(CompositeItemUniteRequestCriteriaUtil.hasCompositeItemId(tuple._2));

                    return mongoTemplate.exists(query, CompositeItemUniteRequest.class)
                            .flatMap(has -> {
                                if (has) {
                                    return Mono.error(new RuntimeException("Composite item unite request for provided items already exists"));
                                }

                                CompositeItemUniteRequest compositeItemUniteRequest = new CompositeItemUniteRequest();
                                compositeItemUniteRequest.setItemId(tuple._1);
                                compositeItemUniteRequest.setCompositeItemId(tuple._2);

                                return mongoTemplate.save(compositeItemUniteRequest);
                            });
                });
    }

    public Flux<CompositeItemUniteRequest> findAllForItem(String itemId) {
        Query query = Query.query(CompositeItemUniteRequestCriteriaUtil.containsItemId(itemId));
        return mongoTemplate.find(query, CompositeItemUniteRequest.class);
    }

    public Mono<CompositeItemUniteRequest> getComposeItemRequest(ObjectId uniteId) {
        Query query = Query.query(CompositeItemUniteRequestCriteriaUtil.hasId(uniteId));

        return mongoTemplate.findOne(query, CompositeItemUniteRequest.class);
    }

    public Mono<Boolean> deleteComposeItemRequest(ObjectId uniteId) {
        Query query = Query.query(CompositeItemUniteRequestCriteriaUtil.hasId(uniteId));

        return mongoTemplate.remove(query, CompositeItemUniteRequest.class)
                .map(result -> result.getDeletedCount() > 0);
    }
}
