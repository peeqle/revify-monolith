package com.revify.monolith.items.model.item;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.geolocation.GeoLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDescription {
    private String title;
    private String description;

    private List<String> categories;
    private String shopReference;
    private String url;

    private Price maximumRequiredBidPrice;
    private GeoLocation destination;

    private Boolean compositeStackingEnabled;
}
