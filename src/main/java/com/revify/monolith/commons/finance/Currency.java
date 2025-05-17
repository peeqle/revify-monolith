package com.revify.monolith.commons.finance;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.serializer.Deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Currency {
    USD("USD", "$"),
    EUR("EUR", "€"),
    GBP("GBP", "£"),
    JPY("JPY", "¥"),
    CNY("CNY", "¥"),
    AUD("AUD", "$"),
    CAD("CAD", "$"),
    CHF("CHF", "CHF"),
    TRY("TRY", "₺"),
    RUB("RUB", "₽"),
    BYN("BYN", "Br"),
    PLN("PLN", "zł");

    private final String name;
    private final String symbol;

    public static Set<String> all() {
        return Arrays.stream(Currency.values()).map(Currency::getName).collect(Collectors.toSet());
    }

    public static Currency from(String name) {
        return Arrays.stream(Currency.values()).filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }
}