package com.revify.monolith.commons.models.bid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionChangesRequest {
    private String itemId;
    private Long changeValidUntil;
    private Boolean isPremium = true;
}
