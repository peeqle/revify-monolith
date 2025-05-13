package com.revify.monolith.commons.items;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.geolocation.GeoLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDescriptionDTO {
    private String title;
    private String description;

    private String category;
    private String shopReference;
    private String url;

    private Price maximumRequiredBidPrice;
    private GeoLocation destination;

    private Boolean compositeStackingEnabled;
}
