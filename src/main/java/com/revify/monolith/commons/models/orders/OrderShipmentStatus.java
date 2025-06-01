package com.revify.monolith.commons.models.orders;

import lombok.Getter;

@Getter
public enum OrderShipmentStatus {
    CREATED,
    PREPARING,
    BOUGHT,
    PACKED,
    ON_THE_WAY,
    TRANSPORTED,
    RECEIVED;
}