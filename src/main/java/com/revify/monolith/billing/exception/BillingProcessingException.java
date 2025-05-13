package com.revify.monolith.billing.exception;


import com.revify.monolith.commons.messaging.dto.BillingCreation;

public class BillingProcessingException extends RuntimeException {
    public BillingProcessingException(BillingCreation value) {
        super(value.toString());
    }
}
