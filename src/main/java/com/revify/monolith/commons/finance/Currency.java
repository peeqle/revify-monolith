package com.revify.monolith.commons.finance;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Currency {
    UNITED_STATES_DOLLAR("USD", "$"),
    EURO("EUR", "€"),
    BRITISH_POUND("GBP", "£"),
    JAPANESE_YEN("JPY", "¥"),
    CHINESE_YUAN("CNY", "¥"),
    AUSTRALIAN_DOLLAR("AUD", "$"),
    CANADIAN_DOLLAR("CAD", "$"),
    SWISS_FRANC("CHF", "CHF"),
    TURKISH_LIRA("TRY", "₺"),
    RUSSIAN_RUBLE("RUB", "₽"),
    BELARUSIAN_RUBLE("BYN", "Br"),
    POLISH_ZLOTY("PLN", "zł");

    private final String name;
    private final String symbol;

    public static Set<String> all() {
        return Arrays.stream(Currency.values()).map(Currency::getName).collect(Collectors.toSet());
    }

    public static Currency from(String name) {
        return Arrays.stream(Currency.values()).filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }
}