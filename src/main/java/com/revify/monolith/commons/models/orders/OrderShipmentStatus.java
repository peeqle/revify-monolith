package com.revify.monolith.commons.models.orders;

import lombok.Getter;

@Getter
public enum OrderShipmentStatus {
    CREATED,
    BOUGHT,
    PACKED,
    AWAITING,
    ON_THE_WAY,
    TRANSPORTED,
    RECEIVED;
}