package com.revify.monolith.items.utils;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.items.Category;
import com.revify.monolith.commons.items.ItemCreationDTO;
import com.revify.monolith.commons.items.ItemDTO;
import com.revify.monolith.commons.items.ItemDescriptionDTO;
import com.revify.monolith.geo.model.GeoLocation;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.model.item.ItemDescription;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.Instant;
import java.util.Collections;
import java.util.stream.Collectors;

public class ItemUtils {

    public static ItemDTO from(Item item) {
        GeoLocation destination = item.getItemDescription().getDestination();
        return new ItemDTO(
                item.getId().toHexString(),
                item.getCreatorId(),
                ItemDescriptionDTO.builder()
                        .description(item.getItemDescription().getDescription())
                        .destination(ItemDescriptionDTO.Destination.builder()
                                .address(destination.getDisplayName())
                                .latitude(destination.getLocation().getY())
                                .longitude(destination.getLocation().getX())
                                .build())
                        .title(item.getItemDescription().getTitle())
                        .maximumRequiredBidPrice(item.getItemDescription().getMaximumRequiredBidPrice())
                        .build(),
                Collections.emptyList(),
                new Price.Builder()
                        .withAmount(item.getPrice().getAmount())
                        .withCurrency(item.getPrice().getCurrency()).build(),
                item.getCreatedAt(),
                item.getUpdatedAt(),
                item.getValidUntil(),
                item.isActive()
        );
    }

    public static Item from(ItemCreationDTO itemCreation) {
        Item item = new Item();

        item.setCreatedAt(Instant.now().toEpochMilli());
        item.setUpdatedAt(Instant.now().toEpochMilli());

        item.setValidUntil(itemCreation.validUntil());
        item.setPrice(itemCreation.price());

        GeoLocation destination = new GeoLocation();
        ItemDescriptionDTO.Destination itemDestination = itemCreation.description().getDestination();
//        destination.setCountryCode(itemDestination.getCountryCode());
//        destination.setPlaceName(itemDestination.getPlaceName());
        destination.setDisplayName(itemDestination.getAddress());
        destination.setLocation(new GeoJsonPoint(itemDestination.getLatitude(), itemDestination.getLongitude()));

        item.setActive(true);
        item.setItemDescription(
                ItemDescription.builder()
                        .description(itemCreation.description().getDescription())
                        .title(itemCreation.description().getTitle())

                        .url(itemCreation.description().getUrl())
                        .categories(itemCreation.description().getCategories().stream().map(Category::valueOf).collect(Collectors.toSet()))
                        .shopReference(itemCreation.description().getShopReference())

                        .compositeStackingEnabled(itemCreation.description().getCompositeStackingEnabled())

                        .destination(destination)
                        .maximumRequiredBidPrice(itemCreation.description().getMaximumRequiredBidPrice())
                        .build()
        );

        return item;
    }
}
