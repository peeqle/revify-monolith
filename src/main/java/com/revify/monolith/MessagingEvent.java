package com.revify.monolith;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class MessagingEvent {
    Long activeAt;
}
