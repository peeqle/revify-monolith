package com.revify.monolith.shoplift.model.dto;

import com.revify.monolith.commons.geolocation.CountryCode;
import com.revify.monolith.commons.items.Category;
import com.revify.monolith.shoplift.model.Shop;

import java.util.Set;

public record ShopDTO(String shopId, String shopName, Set<CountryCode> countries, Set<Category> categories) {
    public static ShopDTO from(Shop shop) {
        return new ShopDTO(shop.getId().toString(), shop.getName(), shop.getCountries(), shop.getCategories());
    }
}
