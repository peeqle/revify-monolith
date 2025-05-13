package com.revify.monolith.commons.messaging.dto.finance;

import com.revify.monolith.commons.finance.Price;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceRequest implements Serializable {
    private Long userId;
    private String itemId;

    private Price insurancePrice;
    private Price itemPrice;
}
