package com.revify.monolith.commons.items;


import com.revify.monolith.commons.finance.Price;

public record ItemCreationDTO(ItemDescriptionDTO description,
                              ItemInsuranceDTO insurance,
                              Long validUntil, Price price) {
}
