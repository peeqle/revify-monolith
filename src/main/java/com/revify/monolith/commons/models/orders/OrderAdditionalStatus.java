package com.revify.monolith.commons.models.orders;

import lombok.Getter;

@Getter
public enum OrderAdditionalStatus {
    CLIENT_PAYMENT_AWAIT,
    PAYMENTS_RECEIVED,
    LOCATION_UPDATED,
    COURIER_CHANGED,
    DELIVERY_TIME_CHANGED,
}
