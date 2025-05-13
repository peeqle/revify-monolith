package com.revify.monolith.billing.utils;

import com.revify.monolith.billing.model.Billing;
import jakarta.persistence.PrePersist;

import java.time.Instant;

public class BillingEntityListener {

    @PrePersist
    public void prePersist(Billing billing) {
        billing.setCreatedAt(Instant.now().toEpochMilli());
    }
}
