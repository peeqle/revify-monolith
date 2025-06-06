package com.revify.monolith.items.model.item;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.items.Category;
import com.revify.monolith.geo.model.GeoLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDescription {
    private String title;
    private String description;

    private Set<Category> categories;
    private List<String> shopReference;
    private String url;

    private Price maximumRequiredBidPrice;
    private GeoLocation destination;

    private Boolean compositeStackingEnabled;
}
