package com.revify.monolith.commons.models.orders;

import lombok.Getter;

@Getter
public enum OrderAdditionalStatus {
    LOCATION_UPDATED,
    COURIER_CHANGED,
    DELIVERY_TIME_CHANGED,
}
