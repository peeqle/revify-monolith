package com.revify.monolith.commons.messaging;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class MessagingEvent {
    Long activeAt;
    String jsonContent;
}
