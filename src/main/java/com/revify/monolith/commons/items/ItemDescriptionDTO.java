package com.revify.monolith.commons.items;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.items.model.item.ItemDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDescriptionDTO {
    private String title;
    private String description;

    private Set<Category> categories;
    private String shopReference;
    private String url;

    private Price maximumRequiredBidPrice;
    private Destination destination;

    private Boolean compositeStackingEnabled;

    @Data
    @Builder
    @AllArgsConstructor
    public static class Destination {
        private String address;
        private Double latitude;
        private Double longitude;
    }

    public static ItemDescriptionDTO from(ItemDescription item) {
        return ItemDescriptionDTO.builder()
                .title(item.getTitle())
                .description(item.getDescription())
                .categories(item.getCategories())
                .shopReference(item.getShopReference())
                .url(item.getUrl())
                .maximumRequiredBidPrice(item.getMaximumRequiredBidPrice())
                .destination(Destination.builder()
                        .address(item.getDestination().getDisplayName())
                        .latitude(item.getDestination().getLocation().getY())
                        .longitude(item.getDestination().getLocation().getX())
                        .build())
                .compositeStackingEnabled(item.getCompositeStackingEnabled())
                .build();
    }
}
