package com.revify.monolith.commons.items;


import com.revify.monolith.commons.finance.Price;

import java.io.Serializable;
import java.util.List;

public record ItemDTO(String id, Long creatorId, ItemDescriptionDTO itemDescription,
                      List<String> referenceUrl, Price price, Long createdAt,
                      Long updatedAt, Long validUntil,
                      boolean isActive) implements Serializable {
}