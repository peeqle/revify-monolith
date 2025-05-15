package com.revify.monolith.items.service.item;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.user.service.ReadUserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
public class ItemReadService {

    private final ReactiveMongoTemplate mongoTemplate;

    private final ReadUserService readUserService;

    public ItemReadService(@Qualifier("reactiveMongoTemplate") ReactiveMongoTemplate mongoTemplate,
                           ReadUserService readUserService) {
        this.mongoTemplate = mongoTemplate;
        this.readUserService = readUserService;
    }

    public Mono<Item> findById(String id) {
        return findById(new ObjectId(id));
    }

    public Mono<Item> findById(ObjectId id) {
        return mongoTemplate.findById(id, Item.class).timeout(Duration.ofMillis(3000));
    }

    public Mono<Item> findByIdAndUser(ObjectId id, Long userId) {
        Query query = Query.query(Criteria.where("_id").is(id).and("creatorId").is(userId))
                .maxTime(Duration.ofMillis(3000));
        return mongoTemplate.findOne(query, Item.class);
    }


    public Flux<Item> findForUser(Integer offset, Integer limit) {
        return removeBlockedByCreatorsCriteria()
                .flux().flatMap(criteria -> {
                    Query query = new Query()
                            .addCriteria(criteria)
                            .skip((long) offset * limit)
                            .limit(limit);

                    return mongoTemplate.find(query, Item.class);
                });
    }

    public Flux<Item> findForUserDestination(Integer offset, Integer limit, Double latitude, Double longitude, Double distance) {
        return removeBlockedByCreatorsCriteria()
                .flux()
                .flatMap(criteria -> {
                    Query query = new Query()
                            .addCriteria(filterByGeolocation(latitude, longitude, distance))
                            .addCriteria(criteria)
                            .skip((long) offset * limit)
                            .limit(limit);
                    return mongoTemplate.find(query, Item.class);
                });
    }

    public Mono<Long> countForLocationRemovingBlockedBy(Double latitude, Double longitude, Double distance) {
        return removeBlockedByCreatorsCriteria()
                .flatMap(criteria -> {
                    Query query = new Query()
                            .addCriteria(filterByGeolocation(latitude, longitude, distance))
                            .addCriteria(criteria);

                    return mongoTemplate.count(query, Item.class);
                });
    }

    public Criteria filterByGeolocation(Double latitude, Double longitude, Double distance) {
        return Criteria.where("itemDescription.destination.location")
                .nearSphere(new Point(longitude, latitude)).maxDistance(distance);
    }

    public Mono<Criteria> removeBlockedByCreatorsCriteria() {
        List<Long> allBlockedBy = readUserService.findAllBlockedBy(UserUtils.getUserId());
        return Mono.just(Criteria.where("creatorId").not()
                .in(allBlockedBy.isEmpty() ? Collections.emptyList() : allBlockedBy));
    }

    public Mono<Long> countAllByCreator(Long userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("creatorId").is(userId));
        return mongoTemplate.count(query, "items");
    }

    //TODO keep till required or replaced
    public Mono<Object> validateAndDeactivateInBackground() {
        return Mono.fromRunnable(() -> {
            mongoTemplate.find(Query.query(Criteria.where("validUntil").lte(Instant.now().toEpochMilli())), Item.class)
                    .subscribe(e -> {
                        e.setActive(false);
                        mongoTemplate.save(e).subscribe();
                    });
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private <T> Mono<T> listenableFutureToMono(ListenableFuture<T> future) {
        return Mono.create(sink ->
                Futures.addCallback(future, new FutureCallback<>() {
                    @Override
                    public void onSuccess(T result) {
                        sink.success(result);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        sink.error(t);
                    }
                }, Runnable::run)
        );
    }
}
