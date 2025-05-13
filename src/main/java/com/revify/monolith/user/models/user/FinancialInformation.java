package com.revify.monolith.user.models.user;

import com.revify.monolith.commons.finance.TaxRegion;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class FinancialInformation implements Serializable {
    private TaxRegion taxRegion;
    private Double overallRate;
    private Double totalSpent;
    private Double totalEarned;
    private Double totalRoundabout;
}
