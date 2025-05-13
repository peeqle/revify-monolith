package com.revify.monolith.commons.models.bid;

import com.revify.monolith.commons.finance.Price;
import lombok.Data;

import java.time.Instant;

@Data
public class BidCreationRequest {
    private String itemId;
    private Price bidPrice;
    private Long createdAt = Instant.now().toEpochMilli();
}
