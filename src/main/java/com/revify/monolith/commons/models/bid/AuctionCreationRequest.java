package com.revify.monolith.commons.models.bid;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.models.Precedence;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionCreationRequest {
    private Long userId;
    private String itemId;
    private PathFragment path;
    private Precedence precedence;
    private AuctionCreationStrategy strategy;
    private Price maximumRequiredBidPrice;

    private Long bidsAcceptingTill;
    private Long deliveryTimeEnd;
    private Long bidsLimit;
    private Long createdAt;

    public enum AuctionCreationStrategy {
        DEFAULT,
        RECREATION
    }

    public static AuctionCreationRequest.AuctionCreationRequestBuilder defaultBuilder() {
        return AuctionCreationRequest.builder()
                .path(PathFragment.defaultBehaviour())
                .precedence(Precedence.DEFAULT)
                .strategy(AuctionCreationStrategy.DEFAULT)
                .createdAt(Instant.now().toEpochMilli());
    }

    public static AuctionCreationRequest.AuctionCreationRequestBuilder recreationBuilder() {
        return AuctionCreationRequest.builder()
                .precedence(Precedence.HIGHEST)
                .strategy(AuctionCreationStrategy.RECREATION)
                .createdAt(Instant.now().toEpochMilli());
    }
}
