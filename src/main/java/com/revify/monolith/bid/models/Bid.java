package com.revify.monolith.bid.models;

import com.revify.monolith.commons.finance.Price;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bids_item")
public class Bid {

    @Id
    private ObjectId id;

    private ObjectId auctionId;

    private Long userId;

    private Price bidPrice;

    private Long createdAt;

    private boolean published = false;
}
