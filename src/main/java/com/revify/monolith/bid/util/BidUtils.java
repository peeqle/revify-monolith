package com.revify.monolith.bid.util;

import com.revify.monolith.bid.models.Bid;
import com.revify.monolith.commons.bids.BidDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BidUtils {

    public static Mono<BidDTO> from(Mono<Bid> emitter) {
        return emitter.map(e -> new BidDTO(e.getId().toHexString(), e.getAuctionId().toHexString(),
                e.getCreatedAt(), e.getBidPrice()));
    }

    public static Flux<BidDTO> from(Flux<Bid> emitter) {
        return emitter.map(e -> new BidDTO(e.getId().toHexString(), e.getAuctionId().toHexString(),
                e.getCreatedAt(), e.getBidPrice()));
    }
}
