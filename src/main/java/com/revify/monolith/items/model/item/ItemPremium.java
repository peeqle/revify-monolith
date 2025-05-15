package com.revify.monolith.items.model.item;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document(collection = "premium")
public class ItemPremium implements Serializable {
    @Id
    private ObjectId id;
    private ObjectId itemId;

    private Long durationUntil;

    private Long createdAt;
    private Long updatedAt;

    private boolean isActive = true;
    private boolean isPayed = true;
}
