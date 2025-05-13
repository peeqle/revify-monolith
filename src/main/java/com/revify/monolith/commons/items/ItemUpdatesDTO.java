package com.revify.monolith.commons.items;


import com.revify.monolith.commons.finance.Price;
import org.bson.types.ObjectId;

public record ItemUpdatesDTO(ObjectId itemId, ItemDescriptionDTO description,
                             Double latitude, Double longitude,
                             Long validUntil, Price price) {
}
