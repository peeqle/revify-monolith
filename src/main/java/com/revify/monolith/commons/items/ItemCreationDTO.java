package com.revify.monolith.commons.items;


import com.revify.monolith.commons.finance.Price;

public record ItemCreationDTO(ItemDescriptionDTO description,
                              ItemInsuranceDTO insurance,
                              Double latitude, Double longitude,
                              Long validUntil, Price price) {
}
