package com.revify.monolith.commons.items;


import com.revify.monolith.commons.finance.Price;

public record ItemCreationDTO(ItemDescriptionDTO description,
                              ItemInsuranceDTO insurance,
                              ItemShopliftDTO shoplift,
                              Long validUntil, Price price) {
}
