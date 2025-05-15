package com.revify.monolith.commons.items;

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
    private Integer selectedPlan;
}
