package com.revify.monolith.history.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("history")

@SuperBuilder
public abstract class HistoryEvent {
    Long actorId;
    String description;

    Long timestamp;
}
