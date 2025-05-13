package com.revify.monolith.finance.model.jpa;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)

@Entity
@DiscriminatorValue("STRIPE")
public class StripePaymentSystemAccount extends PaymentSystemAccount {
    private String paymentsPricing;

    private Boolean detailsSubmitted;

    private Boolean payoutsEnabled;
}


