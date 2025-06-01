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
public class ItemShopliftDTO {
    private Boolean addToShoplift;
    private Price maxDeliveryPerEntity;
    private Long maxDeliveryDate;
}
