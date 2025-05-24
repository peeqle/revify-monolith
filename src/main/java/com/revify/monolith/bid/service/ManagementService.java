package com.revify.monolith.bid.service;

import com.revify.monolith.bid.messaging.OrderProducer;
import com.revify.monolith.bid.models.Auction;
import com.revify.monolith.bid.models.Bid;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.models.bid.BidCreationRequest;
import com.revify.monolith.currency_reader.service.CurrencyService;
import com.revify.monolith.items.service.item.ItemReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagementService {

    private final MongoTemplate mongoTemplate;

    private final CurrencyService currencyService;

    private final AuctionService auctionService;

    private final OrderProducer orderProducer;
    private final ItemReadService itemReadService;

    public Bid findById(ObjectId id) {
        return mongoTemplate.findById(id, Bid.class);
    }

    public Bid findLastBidForAuction(ObjectId auctionId) {
        Query query = Query.query(
                Criteria.where("auctionId").is(auctionId)
                        .and("published").is(true)
        ).with(Sort.by(Sort.Direction.DESC, "createdAt")
                //todo placed bids are always the currency of the items - create it
                .and(Sort.by(Sort.Direction.ASC, "price.amount")));

        return mongoTemplate.findOne(query, Bid.class);
    }

    /**
     * Find last N items DESC
     */
    public List<Bid> findLastBids(String itemId, Integer limit) {
        Auction auction = auctionService.findAuctionForItem(itemId);
        if (auction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found");
        }
        Query query = Query.query(
                Criteria.where("auctionId").is(auction.getId())
                        .and("published").is(true)
        ).limit(limit).with(Sort.by(Sort.Direction.DESC, "createdAt"));

        return mongoTemplate.find(query, Bid.class);
    }

    public Long countLastBids(Auction auction) {
        Query query = Query.query(Criteria.where("auctionId").is(auction.getId())
                .and("published").is(true));
        return mongoTemplate.count(query, Bid.class);
    }

    public Bid findTerminatingBidForItem(ObjectId itemModelId) {
        Query query = Query.query(
                Criteria.where("auctionId").is(itemModelId)
                        .and("published").is(true)
        ).with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.findOne(query, Bid.class);
    }

    public List<Bid> findAllBids(String itemId) {
        Auction auction = auctionService.findAuctionForItem(itemId);
        if (auction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found for item: " + itemId);
        }
        Query query = Query.query(
                Criteria.where("auctionId").is(auction.getId())
                        .and("published").is(true)
        ).with(Sort.by(Sort.Direction.DESC, "createdAt"));

        return mongoTemplate.find(query, Bid.class);
    }

    public Bid createBid(BidCreationRequest bidCreationRequest) {
        Auction auction = auctionService.findAuctionForBid(bidCreationRequest.getItemId(), true);
        if (auction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found for item: " + bidCreationRequest.getItemId());
        }
        if (auction.getCreatorId() == UserUtils.getUserId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot place a bid to your item");
        }
        Bid newBid = Bid.builder()
                .auctionId(auction.getId())
                .bidPrice(bidCreationRequest.getPrice())
                .createdAt(bidCreationRequest.getCreatedAt())
                .build();


        Bid lastBidForAuction = findLastBidForAuction(auction.getId());
        if (lastBidForAuction == null) {
            lastBidForAuction = createFirstBid(auction, newBid);
        } else {
            lastBidForAuction = tryCreateBid(auction, newBid, lastBidForAuction, countLastBids(auction));
        }

        return lastBidForAuction;
    }

    private Bid tryCreateBid(Auction auction, Bid newBid, Bid lastBid, Long count) {
        if (auction.getBidsLimit() == null || count + 1 <= auction.getBidsLimit()) {
            if (lastBid == null) {
                return createFirstBid(auction, newBid);
            }

            if (isLessThanRequired(auction, newBid)) {
                Boolean compare = currencyService
                        .compare(CurrencyService.Operand.LT, newBid.getBidPrice(), lastBid.getBidPrice());
                if (compare) {
                    newBid.setUserId(UserUtils.getUserId());
                    newBid.setCreatedAt(Instant.now().toEpochMilli());
                    newBid.setPublished(true);
                    return mongoTemplate.save(newBid);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Bid must be less or equal than auction max delivery price");
            }
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create new bid, cause bids limit exceeded");
    }

    private boolean isLessThanRequired(Auction auction, Bid newBid) {
        return
                currencyService
                        .compare(CurrencyService.Operand.LT,
                                newBid.getBidPrice(),
                                auction.getMaximumRequiredBidPrice()) ||
                        currencyService
                                .compare(CurrencyService.Operand.EQ,
                                        newBid.getBidPrice(),
                                        auction.getMaximumRequiredBidPrice());
    }

    private Bid createFirstBid(Auction auction, Bid newBid) {
        if (isLessThanRequired(auction, newBid)) {
            newBid.setUserId(UserUtils.getUserId());
            newBid.setCreatedAt(Instant.now().toEpochMilli());
            newBid.setPublished(true);
            return mongoTemplate.save(newBid);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create new bid");
    }

    public Auction finishAuction(ObjectId auctionId, ObjectId selectedBid) {
        Auction auction = auctionService.closeAuction(auctionId);
        if (auction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found for item: " + auctionId);
        }
        if (!auction.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Auction is in active state");
        }

        Bid bid = findById(selectedBid);
        if (bid == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bid is not found for item: " + auctionId);
        }
        orderProducer.createOrder(auction, bid);

        return auction;
    }
}
