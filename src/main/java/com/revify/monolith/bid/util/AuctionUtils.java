package com.revify.monolith.bid.util;

import com.revify.monolith.bid.models.Auction;
import com.revify.monolith.commons.bids.AuctionDTO;
import reactor.core.publisher.Mono;

public class AuctionUtils {

    public static Mono<AuctionDTO> from(Mono<Auction> emitter) {
        return emitter.map(e -> new AuctionDTO(
                e.getId().toHexString(),
                e.getItemId(),
                e.getCreatorId(),
                e.getBidsAcceptingTill(),
                e.getMaximumRequiredBidPrice())
        );
    }
}
