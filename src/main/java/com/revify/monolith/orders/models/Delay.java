package com.revify.monolith.orders.models;

import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Document("order_delay")
public class Delay {
    @Id
    private ObjectId id;

    private final ObjectId orderId;
    private final Integer particleIndex;

    private final Long createdAt;

    @Builder
    private Delay(ObjectId orderId, Integer particleIndex) {
        this.orderId = orderId;
        this.particleIndex = particleIndex;

        this.createdAt = Instant.now().toEpochMilli();
    }
}
