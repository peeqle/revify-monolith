package com.revify.monolith.items.model.util;


import com.revify.monolith.commons.items.Category;
import com.revify.monolith.geo.model.GeoLocation;
import com.revify.monolith.commons.items.ItemDescriptionDTO;
import com.revify.monolith.items.model.item.ItemDescription;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.stream.Collectors;

import static com.revify.monolith.items.model.util.ComparisonUtils.*;

public class ItemChangesComparator {

    public static int compareDto(ItemDescription o1, ItemDescriptionDTO o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        int result = compareNullableStrings(o1.getTitle(), o2.getTitle());
        if (result != 0) return result;

        result = compareNullableStrings(o1.getDescription(), o2.getDescription());
        if (result != 0) return result;

        result = compareNullableStrings(o1.getUrl(), o2.getUrl());
        if (result != 0) return result;

        result = compareNullablePrices(o1.getMaximumRequiredBidPrice(), o2.getMaximumRequiredBidPrice());
        if (result != 0) return result;

        result = o2.getDestination().getLatitude().compareTo(o1.getDestination().getLocation().getY());
        if (result != 0) return result;

        result = o2.getDestination().getLongitude().compareTo(o1.getDestination().getLocation().getX());
        if (result != 0) return result;

        result = compareNullableBooleans(o1.getCompositeStackingEnabled(), o2.getCompositeStackingEnabled());
        return result;
    }


    public static ItemDescription merge(ItemDescription initial, ItemDescriptionDTO dto) {
        if (dto == null) return initial;

        if (dto.getTitle() != null) {
            initial.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            initial.setDescription(dto.getDescription());
        }
        if (dto.getCategories() != null) {
            initial.setCategories(dto.getCategories());
        }
        if (dto.getShopReference() != null) {
            initial.setShopReference(dto.getShopReference());
        }
        if (dto.getUrl() != null) {
            initial.setUrl(dto.getUrl());
        }
        if (dto.getMaximumRequiredBidPrice() != null) {
            initial.setMaximumRequiredBidPrice(dto.getMaximumRequiredBidPrice());
        }
        if (dto.getDestination() != null) {
            GeoLocation destination = initial.getDestination();
            destination.setLocation(new GeoJsonPoint(dto.getDestination().getLongitude(), dto.getDestination().getLatitude()));

            initial.setDestination(destination);
        }
        if (dto.getCompositeStackingEnabled() != null) {
            initial.setCompositeStackingEnabled(dto.getCompositeStackingEnabled());
        }

        return initial;
    }

    public static int compare(ItemDescription o1, ItemDescription o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        int result = compareNullableStrings(o1.getTitle(), o2.getTitle());
        if (result != 0) return result;

        result = compareNullableStrings(o1.getDescription(), o2.getDescription());
        if (result != 0) return result;

        result = compareNullableStrings(o1.getUrl(), o2.getUrl());
        if (result != 0) return result;

        result = compareNullablePrices(o1.getMaximumRequiredBidPrice(), o2.getMaximumRequiredBidPrice());
        if (result != 0) return result;

        result = compareNullableGeoLocations(o1.getDestination(), o2.getDestination());
        if (result != 0) return result;

        result = compareNullableBooleans(o1.getCompositeStackingEnabled(), o2.getCompositeStackingEnabled());
        return result;
    }
}
