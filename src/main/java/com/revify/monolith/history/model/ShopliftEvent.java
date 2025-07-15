package com.revify.monolith.history.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)

@SuperBuilder
public class ShopliftEvent extends HistoryEvent {
    private String shopliftId;
}

