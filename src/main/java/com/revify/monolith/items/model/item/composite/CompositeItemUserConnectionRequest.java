package com.revify.monolith.items.model.item.composite;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Data
@Document(value = "composite_item_connection_request")
public class CompositeItemUserConnectionRequest {
    @Id
    private ObjectId id;

    private Long userId;
    private Long inviterId;

    @DocumentReference
    private CompositeItem compositeItem;

    private CompositionStatus compositionStatus = CompositionStatus.PENDING;

    private Long createdAt = Instant.now().toEpochMilli();
    @Indexed(expireAfterSeconds = 0)
    private Long validUntil = Instant.now().plus(3, ChronoUnit.DAYS).toEpochMilli();
}
