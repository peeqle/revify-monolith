package com.revify.monolith.commons.finance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaxRegion {
    BELARUS(13.0),
    RUSSIA(13.0),
    POLAND(12.0);

    private final Double taxRate;
}
