package com.revify.monolith.bid.service;


import com.revify.monolith.bid.models.Auction;
import com.revify.monolith.bid.models.Bid;
import com.revify.monolith.bid.models.exceptions.ItemDoesPersistException;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.models.bid.AuctionChangesRequest;
import com.revify.monolith.commons.models.bid.AuctionCreationRequest;
import com.revify.monolith.commons.models.bid.AuctionToggleRequest;
import com.revify.monolith.commons.models.bid.BidCreationRequest;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuctionService {

    private final ReactiveRedisTemplate<String, Object> ttlRedisTemplate;

    private final ReactiveMongoTemplate mongoTemplate;

    public AuctionService(ReactiveMongoTemplate mongoTemplate,
                          @Qualifier("ttlRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.ttlRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Auction> createAuction(Mono<AuctionCreationRequest> bidItemModelCreationRequest) {
        return bidItemModelCreationRequest
                .flatMap(this::validateItemNotExists)
                .map(this::buildAuction)
                .flatMap(mongoTemplate::save)
                .doOnError(e -> System.err.println("Error during save: " + e.getMessage()))
                .flatMap(e -> {
                    saveToRedis(e);
                    return Mono.just(e);
                });
    }

    public Mono<Auction> recreateAuction(Mono<AuctionCreationRequest> bidItemModelCreationRequest) {
        return bidItemModelCreationRequest
                .flatMap(this::validateItemNotExists)
                .map(this::buildAuction)
                .flatMap(mongoTemplate::save)
                .doOnError(e -> System.err.println("Error during save: " + e.getMessage()))
                .flatMap(e -> {
                    saveToRedis(e);
                    return Mono.just(e);
                });
    }

    public Mono<Auction> deactivateAuction(AuctionToggleRequest auctionToggleRequest) {
        return mongoTemplate.findOne(Query.query(Criteria.where("itemId").is("itemId")), Auction.class)
                .flatMap(auction -> {
                    auction.setIsActive(auctionToggleRequest.getStatus());
                    auction.setManuallyToggled(auctionToggleRequest.getManuallyToggled());

                    return mongoTemplate.save(auction);
                });
    }

    public Mono<Auction> changeAuction(AuctionChangesRequest auctionChangesRequest) {
        return mongoTemplate.findOne(Query.query(Criteria.where("itemId").is("itemId")), Auction.class)
                .flatMap(auction -> {
                    auction.setIsActive(true);
                    auction.setManuallyToggled(false);
                    auction.setBidsAcceptingTill(auctionChangesRequest.getChangeValidUntil());
                    auction.setIsPremium(auctionChangesRequest.getIsPremium());

                    return mongoTemplate.save(auction);
                });
    }


    //todo notify all related to auction
    public Mono<Auction> closeAuction(ObjectId auctionId) {
        return findAuction(auctionId)
                .map(e -> {
                    e.setIsActive(false);
                    return e;
                })
                .flatMap(mongoTemplate::save);
    }

    public Mono<Auction> archiveAuction(ObjectId auctionId) {
        return findAuction(auctionId)
                .map(e -> {
                    e.setIsArchived(true);
                    e.setIsActive(false);
                    return e;
                })
                .flatMap(mongoTemplate::save);
    }

    private Mono<AuctionCreationRequest> validateItemNotExists(AuctionCreationRequest request) {
        return findAuctionForItemIdAndUserId(request.getItemId(), request.getUserId())
                .hasElement()
                .flatMap(hasElement -> {
                    if (hasElement) {
                        return Mono.error(new ItemDoesPersistException("Item bid already exists"));
                    }
                    return Mono.just(request);
                })
                .onErrorMap(ItemDoesPersistException.class,
                        ex -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
    }

    public Mono<Tuple2<Bid, Auction>> searchForBidModel(BidCreationRequest creation) {
        long userId = UserUtils.getUserId();
        return findAuctionForItemIdAndUserId(creation.getItemId(), userId)
                .onErrorMap(IllegalArgumentException.class, ex -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex))
                .map(bidModel -> Tuples.of(Bid.builder()
                        .auctionId(bidModel.getId())
                        .userId(userId)
                        .bidPrice(creation.getBidPrice())
                        .build(), bidModel)
                );
    }

    public Mono<Auction> findAuctionForItemId(String itemId) {
        Query query = Query.query(
                Criteria.where("itemId").is(itemId)
                        .and("bidsAcceptingTill")
                        .gte(Instant.now().plus(5, TimeUnit.SECONDS.toChronoUnit()).toEpochMilli())
        );

        return mongoTemplate.findOne(query, Auction.class);
    }

    public Mono<Auction> findAuction(String auctionId) {
        return findAuction(new ObjectId(auctionId));
    }

    public Mono<Auction> findAuction(ObjectId auctionId) {
        Query query = Query.query(Criteria.where("id").is(auctionId));
        return mongoTemplate.findOne(query, Auction.class);
    }

    public Mono<Boolean> isItemCreatedByUser(ObjectId itemId) {
        Query query = Query.query(Criteria.where("itemId").is(itemId));

        return mongoTemplate.findOne(query, Auction.class)
                .map(item -> item.getCreatorId().equals(UserUtils.getUserId()));
    }

    public Mono<Auction> findAuctionForItemIdAndUserId(String itemId, Long userId) {
        Query query = Query.query(
                Criteria.where("itemId").is(itemId)
                        .and("creatorId").ne(userId)
                        .and("isActive").is(true)
                        .and("bidsAcceptingTill")
                        .gte(Instant.now().plus(2, TimeUnit.SECONDS.toChronoUnit()).toEpochMilli())
        );

        return mongoTemplate.findOne(query, Auction.class);
    }

    private Auction buildAuction(AuctionCreationRequest request) {
        return Auction.builder()
                .itemId(request.getItemId())
                .bidsAcceptingTill(request.getBidsAcceptingTill())
                .creatorId(request.getUserId())
                .maximumRequiredBidPrice(request.getMaximumRequiredBidPrice())
                .bidsLimit(request.getBidsLimit())
                .isActive(true)
                .manuallyToggled(false)
                .build();
    }

    private void saveToRedis(Auction model) {
        long ttl = model.getBidsAcceptingTill() - Instant.now().toEpochMilli();
        ttlRedisTemplate.opsForValue()
                .set(model.getId().toHexString(), model.getBidsAcceptingTill(), Duration.ofMillis(ttl))
                .subscribe(success -> {
                    if (success) {
                        System.out.println("Entry set with expiration " + model.getId().toHexString());
                    } else {
                        System.out.println("Failed to set entry" + model.getId().toHexString());
                    }
                });
    }
}
