package com.revify.monolith.commons.models.bid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionToggleRequest {
    private String itemId;
    private Boolean status;
    private Boolean manuallyToggled;
}
