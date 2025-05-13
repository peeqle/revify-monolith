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
public class ItemPremiumBillingCreation extends BillingCreation {
    private String itemPremiumId;
}