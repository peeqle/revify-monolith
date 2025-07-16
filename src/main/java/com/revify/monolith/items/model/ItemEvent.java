package com.revify.monolith.items.model;

import com.revify.monolith.MessagingEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder

@EqualsAndHashCode(callSuper = true)
public class ItemEvent extends MessagingEvent {
    ItemEventType type;

    public enum ItemEventType {
        UPDATE,
        FINISH,
    }
}
