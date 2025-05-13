package com.revify.monolith.commons.bids;


import com.revify.monolith.commons.finance.Price;

public record AuctionDTO(String auctionId, String itemId, Long creatorId,
                         Long bidsAcceptingTill, Price maximumRequiredBidPrice) {
}
