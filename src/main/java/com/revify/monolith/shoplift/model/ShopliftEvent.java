package com.revify.monolith.shoplift.model;

import com.revify.monolith.commons.messaging.MessagingEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder

@EqualsAndHashCode(callSuper = true)
public class ShopliftEvent extends MessagingEvent {
    ShopliftEventType type;

    public enum ShopliftEventType {
        ITEMS_CHANGED,
        LOCATION_CHANGE,
        UPDATE,
        FINISH,
    }
}
