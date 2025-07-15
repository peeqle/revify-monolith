package com.revify.monolith.shoplift.model.dto;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.items.Category;
import com.revify.monolith.geo.model.GeoLocation;
import com.revify.monolith.shoplift.model.Shoplift;

import java.util.List;
import java.util.Set;

public record ShopliftDTO(String id, String title, String description, List<ShopDTO> shops,
                          Long creatorId, Set<Long> involvedCourierIds, GeoLocation destination,
                          Set<Category> presentCategories, Set<Category> projectedCategories,
                          Set<String> items,
                          Price minEntryDeliveryPrice, Price maxEntryPrice, Integer entries, Long deliveryCutoffTime,
                          Long createdAt, Long updatedAt, Boolean isRecurrent, Boolean allowedSystemAppend) {
    public static ShopliftDTO from(Shoplift shoplift, List<ShopDTO> shops) {
        return new ShopliftDTO(
                shoplift.getId().toHexString(),
                shoplift.getTitle(),
                shoplift.getDescription(),
                shops,
                shoplift.getCreatorId(),
                shoplift.getInvolvedCourierIds(),
                shoplift.getDestination(),
                shoplift.getPresentCategories(),
                shoplift.getProjectedCategories(),
                shoplift.getConnectedItems(),
                shoplift.getMinEntryDeliveryPrice(),
                shoplift.getMaxEntryPrice(),
                shoplift.getEntries(),
                shoplift.getDeliveryCutoffTime(),
                shoplift.getCreatedAt(),
                shoplift.getUpdatedAt(),
                shoplift.getIsRecurrent(),
                shoplift.getAllowedSystemAppend()
        );
    }
}
