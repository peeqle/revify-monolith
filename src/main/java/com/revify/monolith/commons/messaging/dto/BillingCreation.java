package com.revify.monolith.commons.messaging.dto;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.finance.TaxRegion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BillingCreation {
    private Long payerId;

    private Price price;
    private TaxRegion taxRegion;
    private BillingStrategy billingStrategy = BillingStrategy.INITIAL;

    public enum BillingStrategy {
        INITIAL,
        REFUND
    }
}
