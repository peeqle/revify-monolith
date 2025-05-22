package com.revify.monolith.commons.bids;


import com.revify.monolith.bid.models.Auction;
import com.revify.monolith.commons.finance.Price;

public record AuctionDTO(String auctionId, String itemId, Long creatorId,
                         Boolean isActive,  Boolean isArchived, Boolean isPremium,
                         Long bidsAcceptingTill, Price maximumRequiredBidPrice) {

    public static AuctionDTO from(Auction auction) {
        return new AuctionDTO(auction.getId().toHexString(), auction.getItemId(), auction.getCreatorId(),
                auction.getIsActive(), auction.getIsArchived(), auction.getIsPremium(),
                auction.getBidsAcceptingTill(), auction.getMaximumRequiredBidPrice());
    }
}
