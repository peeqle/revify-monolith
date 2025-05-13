package com.revify.monolith.commons.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor

@EqualsAndHashCode(callSuper = true)
public class InsuranceBillingCreation extends BillingCreation {
    private String insuranceId;
}


