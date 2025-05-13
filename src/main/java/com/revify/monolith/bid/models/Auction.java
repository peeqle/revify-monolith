package com.revify.monolith.bid.models;

import com.revify.monolith.commons.finance.Currency;
import com.revify.monolith.commons.finance.Price;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "auctions")
public class Auction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private ObjectId id;
    private String itemId;
    private Long creatorId;

    //bids starting point
    private Price maximumRequiredBidPrice = new Price(Currency.EURO, new BigDecimal(Double.MAX_VALUE));

    private Long bidsLimit;
    private Long bidsAcceptingTill;

    private Long deliveryTimeEnd;

    private Boolean isActive;
    private Boolean manuallyToggled;

    private Boolean isArchived;

    private Boolean isPremium = false;
}
