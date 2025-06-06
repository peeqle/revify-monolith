package com.revify.monolith.commons.items;


import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.items.model.item.Item;

import java.io.Serializable;
import java.util.List;

public record ItemDTO(String id, Long creatorId, ItemDescriptionDTO itemDescription,
                      List<String> referenceUrl, Price price, Long createdAt,
                      List<String> shopReference, String url,
                      Long updatedAt, Long validUntil,
                      boolean isActive, String shopliftId) implements Serializable {

    public static ItemDTO from(Item item) {
        return new ItemDTO(item.getId().toHexString(), item.getCreatorId(), ItemDescriptionDTO.from(item.getItemDescription()),
                item.getReferenceUrl(), item.getPrice(), item.getCreatedAt(), item.getItemDescription().getShopReference(),
                item.getItemDescription().getUrl(),
                item.getUpdatedAt(), item.getValidUntil(), item.isActive(),
                item.getShopliftId());
    }
}