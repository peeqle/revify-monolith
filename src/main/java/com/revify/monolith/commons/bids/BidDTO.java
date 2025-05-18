package com.revify.monolith.commons.bids;


import com.revify.monolith.bid.models.Bid;
import com.revify.monolith.commons.finance.Price;

public record BidDTO(String id, String auctionId, Long userId, Long createdAt, Price price) {

    public static BidDTO from(Bid bid) {
        return new BidDTO(bid.getId().toHexString(), bid.getAuctionId().toHexString(), bid.getUserId(), bid.getCreatedAt(), bid.getBidPrice());
    }
}
