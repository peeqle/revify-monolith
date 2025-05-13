package com.revify.monolith.finance.model.jpa;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)

@Entity
@DiscriminatorValue("BE_PAID")
public class BePaidPaymentSystemAccount extends PaymentSystemAccount {

    private Boolean payoutsEnabled;
}


