package com.revify.monolith.history.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)

@SuperBuilder
public class ShopliftItemEvent extends HistoryEvent {
    private String shopliftId;
    private String itemId;

    private EventType type;

    public enum EventType {
        ORDER_CREATION,
    }
}

