package com.revify.monolith.bid.service;

import com.revify.monolith.bid.messaging.OrderProducer;
import com.revify.monolith.bid.models.Auction;
import com.revify.monolith.bid.models.Bid;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.models.bid.BidCreationRequest;
import com.revify.monolith.currency_reader.service.CurrencyService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Instant;

@Slf4j
@Service
public class ManagementService {

    private final ReactiveMongoTemplate mongoTemplate;

    private final CurrencyService currencyService;

    private final AuctionService auctionService;

    private final OrderProducer orderProducer;

    @Autowired
    public ManagementService(@Qualifier("bidsMongoTemplate") ReactiveMongoTemplate mongoTemplate, CurrencyService currencyService, AuctionService auctionService, OrderProducer orderProducer) {
        this.mongoTemplate = mongoTemplate;
        this.currencyService = currencyService;
        this.auctionService = auctionService;
        this.orderProducer = orderProducer;
    }

    public Mono<Bid> findById(ObjectId id) {
        return mongoTemplate.findById(id, Bid.class);
    }

    public Mono<Bid> findLastBidForItemModel(ObjectId itemModelId) {
        Query query = Query.query(
                Criteria.where("auctionId").is(itemModelId)
                        .and("published").is(true)
        ).with(Sort.by(Sort.Direction.DESC, "createdAt"));

        return mongoTemplate.findOne(query, Bid.class);
    }

    /**
     * Find last N items DESC
     *
     * @param forItem - first itemId, second number of elemnts
     * @return
     */
    public Flux<Bid> findLastBids(Mono<Tuple2<String, Integer>> forItem) {
        return forItem.flatMapMany(e ->
                auctionService.findAuctionForItemId(e.getT1())
                        .flatMapMany(auction -> {
                            Query query = Query.query(
                                    Criteria.where("auctionId").is(auction.getId())
                                            .and("published").is(true)
                            ).limit(e.getT2()).with(Sort.by(Sort.Direction.DESC, "createdAt"));

                            return mongoTemplate.find(query, Bid.class);
                        }).onErrorMap(IllegalArgumentException.class, ex -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex))
        );
    }

    public Mono<Long> countLastBids(Auction bidItemModel) {
        Query query = Query.query(Criteria.where("auctionId").is(bidItemModel.getId())
                .and("published").is(true));
        return mongoTemplate.count(query, Bid.class);
    }

    public Mono<Bid> findTerminatingBidForItem(ObjectId itemModelId) {
        Query query = Query.query(
                Criteria.where("auctionId").is(itemModelId)
                        .and("published").is(true)
        ).limit(1).with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.findOne(query, Bid.class);
    }

    public Flux<Bid> findAllBids(Mono<String> forItem) {
        return forItem
                .flatMapMany(e -> auctionService.findAuctionForItemId(e)
                        .flatMapMany(auction -> {
                            Query query = Query.query(
                                    Criteria.where("auctionId").is(auction.getId())
                                            .and("published").is(true)
                            ).with(Sort.by(Sort.Direction.DESC, "createdAt"));

                            return mongoTemplate.find(query, Bid.class);
                        }).onErrorMap(IllegalArgumentException.class, ex -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex))
                );
    }

    public Mono<Bid> createBid(@NonNull Mono<BidCreationRequest> bidCreationRequest) {
        return auctionService.searchForBidModel(bidCreationRequest)
                .switchIfEmpty(Mono.error(new RuntimeException("Cannot create bid for non existing item")))
                .flatMap(modelTuple ->
                        findLastBidForItemModel(modelTuple.getT2().getId())
                                .switchIfEmpty(createFirstBid(modelTuple.getT2(), modelTuple.getT1()))
                                .flatMap(lastBid -> countLastBids(modelTuple.getT2())
                                        .flatMap(count -> tryCreateBid(modelTuple, lastBid, count)))
                )
                .onErrorMap(RuntimeException.class, ex -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
    }

    private Mono<Bid> tryCreateBid(Tuple2<Bid, Auction> modelTuple, Bid lastBid, Long count) {
        if (modelTuple.getT2().getBidsLimit() == null || count + 1 <= modelTuple.getT2().getBidsLimit()) {
            if (lastBid == null) {
                return Mono.just(modelTuple.getT1());
            }
            return Mono.zip(
                            //check if bid is less than required max amount by the item publisher
                            Mono.zip(currencyService
                                                    .compare(CurrencyService.Operand.LT, modelTuple.getT1().getBidPrice(), modelTuple.getT2().getMaximumRequiredBidPrice())
                                            , currencyService
                                                    .compare(CurrencyService.Operand.EQ, modelTuple.getT1().getBidPrice(), modelTuple.getT2().getMaximumRequiredBidPrice()))
                                    .map(e -> e.getT1() || e.getT2())
                                    .flatMap(isLessThanBaseRequest -> {
                                        if (!isLessThanBaseRequest) {
                                            return Mono.error(new IllegalArgumentException("Bid is greater than base requested price"));
                                        }
                                        return Mono.just(true);
                                    }),
                            //check if bod is less than previous price
                            currencyService
                                    .compare(CurrencyService.Operand.LT, modelTuple.getT1().getBidPrice(), lastBid.getBidPrice())
                                    .flatMap(isLessThanLastBidPrice -> {
                                        if (!isLessThanLastBidPrice) {
                                            return Mono.error(new IllegalArgumentException("Bid is greater than last bid price"));
                                        }
                                        return Mono.just(true);
                                    })
                    )
                    .map(sad -> sad.getT2() && sad.getT1())
                    .map(x -> modelTuple.getT1())
                    .flatMap(validatedLastBid -> {
                        validatedLastBid.setUserId(UserUtils.getUserId());
                        validatedLastBid.setCreatedAt(Instant.now().toEpochMilli());
                        validatedLastBid.setPublished(true);
                        return mongoTemplate.save(validatedLastBid);
                    });
        }
        return Mono.error(new RuntimeException("Cannot create new bid, cause bids limit exceeded"));
    }

    private Mono<Bid> createFirstBid(Auction auction, Bid lastBid) {
        return Mono.zip(currencyService
                                .compare(CurrencyService.Operand.LT, lastBid.getBidPrice(), auction.getMaximumRequiredBidPrice())
                        , currencyService
                                .compare(CurrencyService.Operand.EQ, lastBid.getBidPrice(), auction.getMaximumRequiredBidPrice()))
                .map(x -> x.getT1() || x.getT2())
                .flatMap(isLessThanBaseRequest -> {
                    if (!isLessThanBaseRequest) {
                        return Mono.error(new IllegalArgumentException("Bid is greater than base requested price"));
                    }
                    return Mono.just(true);
                })
                .map(bid -> lastBid)
                .flatMap(bid -> {
                    bid.setUserId(UserUtils.getUserId());
                    bid.setCreatedAt(Instant.now().toEpochMilli());
                    bid.setPublished(true);
                    return mongoTemplate.save(bid);
                });
    }

    public Mono<Void> finishAuction(ObjectId auctionId, ObjectId selectedBid) {
        return auctionService.closeAuction(auctionId)
                .flatMap(auction ->
                        findById(selectedBid)
                                .doOnError((s) -> log.error("Failed to finish auction {}", auctionId.toHexString(), s))
                                .doOnSuccess(bid -> orderProducer.createOrder(auction, bid))).then();
    }
}
