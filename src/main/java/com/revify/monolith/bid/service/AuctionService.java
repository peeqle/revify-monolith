package com.revify.monolith.bid.service;

import com.revify.monolith.bid.models.Auction;
import com.revify.monolith.bid.models.Bid;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.models.bid.AuctionChangesRequest;
import com.revify.monolith.commons.models.bid.AuctionCreationRequest;
import com.revify.monolith.commons.models.bid.AuctionToggleRequest;
import com.revify.monolith.commons.models.bid.BidCreationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.util.function.Tuple2;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionService {

    private final MongoTemplate mongoTemplate;

    private final RedisTemplate<String, Object> ttlRedisTemplate;

    public Auction createAuction(AuctionCreationRequest creationRequest) {
        if (checkActiveAuctionExists(creationRequest.getItemId())) {
            throw new RuntimeException("Active auction already exists for item: " + creationRequest.getItemId());
        }

        Auction newAuction = buildAuction(creationRequest);
        Auction savedAuction = mongoTemplate.save(newAuction);
        saveToRedis(savedAuction);
        return savedAuction;
    }

    public Auction recreateAuction(AuctionCreationRequest creationRequest) {
        Auction latestInactiveAuction = findAuctionByItemUserAndStatus(creationRequest.getItemId(), false);

        if (latestInactiveAuction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Latest inactive auction not found for item: " + creationRequest.getItemId());
        }

        latestInactiveAuction.setUpdatedAt(Instant.now().toEpochMilli());
        latestInactiveAuction.setBidsLimit(creationRequest.getBidsLimit());
        latestInactiveAuction.setDeliveryTimeEnd(creationRequest.getDeliveryTimeEnd());
        latestInactiveAuction.setMaximumRequiredBidPrice(creationRequest.getMaximumRequiredBidPrice());
        latestInactiveAuction.setBidsAcceptingTill(creationRequest.getBidsAcceptingTill());

        Auction savedAuction = mongoTemplate.save(latestInactiveAuction);
        saveToRedis(savedAuction);
        return savedAuction;
    }

    public Auction toggleAuctionStatus(AuctionToggleRequest auctionToggleRequest) {
        Auction auction = findActiveAuctionForUser(auctionToggleRequest.getItemId());

        if (auction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active auction not found for item: " + auctionToggleRequest.getItemId());
        }

        auction.setUpdatedAt(Instant.now().toEpochMilli());
        auction.setIsActive(auctionToggleRequest.getStatus());
        auction.setManuallyToggled(auctionToggleRequest.getManuallyToggled());

        return mongoTemplate.save(auction);
    }

    public Auction changeAuction(AuctionChangesRequest auctionChangesRequest) {
        Auction auction = findActiveAuctionForUser(auctionChangesRequest.getItemId());
        if (auction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active auction not found for item: " + auctionChangesRequest.getItemId());
        }

        auction.setUpdatedAt(Instant.now().toEpochMilli());
        auction.setIsActive(auctionChangesRequest.getIsActive());
        auction.setBidsAcceptingTill(auctionChangesRequest.getChangeValidUntil());
        auction.setIsPremium(auctionChangesRequest.getIsPremium());

        return mongoTemplate.save(auction);
    }

    private Auction findActiveAuctionForUser(String itemId) {
        Query query = Query.query(Criteria.where("itemId").is(itemId)
                .and("isActive").is(true)
                .and("creatorId").is(UserUtils.getUserId()));

        return mongoTemplate.findOne(query, Auction.class);
    }

    public Auction closeAuction(ObjectId auctionId) {
        Auction auction = findAuctionByIdAndUser(auctionId);
        if (auction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found with ID: " + auctionId);
        }

        auction.setUpdatedAt(Instant.now().toEpochMilli());
        auction.setIsActive(false);

        return mongoTemplate.save(auction);
    }

    public Auction archiveAuction(ObjectId auctionId) {
        Auction auction = findAuctionByIdAndUser(auctionId);
        if (auction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found with ID: " + auctionId);
        }

        auction.setUpdatedAt(Instant.now().toEpochMilli());
        auction.setIsActive(false);
        auction.setIsArchived(true);

        return mongoTemplate.save(auction);
    }

    private Auction findAuctionByIdAndUser(ObjectId auctionId) {
        Query query = Query.query(Criteria.where("id").is(auctionId)
                .and("creatorId").is(UserUtils.getUserId()));
        return mongoTemplate.findOne(query, Auction.class);
    }

    public Tuple2<Bid, Auction> searchForBid(BidCreationRequest creation) {
        Auction auction = findActiveAuctionForUser(creation.getItemId());
        if (auction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active auction not found for item: " + creation.getItemId());
        }

        throw new UnsupportedOperationException("Bid creation logic needs to be implemented in searchForBid");
    }

    public Auction findAuction(String auctionId) {
        return findAuction(new ObjectId(auctionId));
    }

    public Auction findAuction(ObjectId auctionId) {
        Query query = Query.query(Criteria.where("id").is(auctionId));
        return mongoTemplate.findOne(query, Auction.class);
    }

    public Boolean isAuctionForItemCreatedByUser(String itemId) {
        Long currentUserId = UserUtils.getUserId();
        Query query = Query.query(Criteria.where("itemId").is(itemId)
                .and("isActive").is(true)
                .and("creatorId").is(currentUserId));

        return mongoTemplate.exists(query, Auction.class);
    }

    public Boolean isAuctionCreatedByUser(ObjectId auctionId) {
        return isAuctionForItemCreatedByUser(auctionId.toHexString());
    }

    public Boolean isAuctionCreatedByUser(String auctionId) {
        Long currentUserId = UserUtils.getUserId();
        Query query = Query.query(Criteria.where("auctionId").is(auctionId)
                .and("isActive").is(true)
                .and("creatorId").is(currentUserId));

        return mongoTemplate.exists(query, Auction.class);
    }

    public Auction findAuctionByItemUserAndStatus(String itemId, Boolean isActive) {
        Query query = Query.query(
                Criteria.where("itemId").is(itemId)
                        .and("creatorId").is(UserUtils.getUserId())
                        .and("isActive").is(isActive)
                        .and("bidsAcceptingTill")
                        .gte(Instant.now().toEpochMilli())
        );

        return mongoTemplate.findOne(query, Auction.class);
    }

    public Auction findAuctionForBid(String itemId, Boolean isActive) {
        Query query = Query.query(
                Criteria.where("itemId").is(itemId)
                        .and("isActive").is(isActive)
                        .and("bidsAcceptingTill")
                        .gte(Instant.now().toEpochMilli())
        );

        return mongoTemplate.findOne(query, Auction.class);
    }

    public boolean checkActiveAuctionExists(String itemId) {
        Query query = Query.query(
                Criteria.where("itemId").is(itemId)
                        .and("isActive").is(true)
                        .and("bidsAcceptingTill")
                        .gte(Instant.now().toEpochMilli())
        );

        return mongoTemplate.exists(query, Auction.class);
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
                .createdAt(Instant.now().toEpochMilli())
                .updatedAt(Instant.now().toEpochMilli())
                .build();
    }

    private void saveToRedis(Auction model) {
        long ttl = model.getBidsAcceptingTill() - Instant.now().toEpochMilli();
        if (ttl > 0) {
            ttlRedisTemplate.opsForValue().set(model.getId().toHexString(), model.getBidsAcceptingTill(), ttl, TimeUnit.MILLISECONDS);
        } else {
            log.warn("Auction {} has a bidsAcceptingTill time in the past. Not saving to Redis with TTL.", model.getId());
            ttlRedisTemplate.opsForValue().set(model.getId().toHexString(), model.getBidsAcceptingTill());
        }
    }
}
