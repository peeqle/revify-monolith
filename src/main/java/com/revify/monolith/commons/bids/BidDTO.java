package com.revify.monolith.commons.bids;


import com.revify.monolith.commons.finance.Price;

public record BidDTO(String bidId, String auctionId, Long createdAt, Price price) {
}
