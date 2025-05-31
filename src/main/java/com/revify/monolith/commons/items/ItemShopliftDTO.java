package com.revify.monolith.commons.items;


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
    private Double maxDeliveryPerEntity;
    private Long maxDeliveryDate;
}
