package com.revify.monolith.finance.service.utils;


import com.revify.monolith.commons.finance.Price;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtils {

    public static long convertToCents(Price price) {
        BigDecimal value = price.getAmount().setScale(2, RoundingMode.HALF_UP);
        return value.multiply(BigDecimal.valueOf(100)).longValue();
    }
}
