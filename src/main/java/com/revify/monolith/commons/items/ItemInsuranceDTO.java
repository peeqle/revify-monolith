package com.revify.monolith.commons.items;

import com.revify.monolith.commons.finance.Price;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInsuranceDTO {
    private Boolean isEnabled;
    private Price price;
    //todo specific cases of insurance
}
