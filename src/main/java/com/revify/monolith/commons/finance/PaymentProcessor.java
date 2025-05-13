package com.revify.monolith.commons.finance;

import com.revify.monolith.commons.geolocation.CountryCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static com.revify.monolith.commons.geolocation.CountryCode.*;


@Getter
@AllArgsConstructor
public enum PaymentProcessor {

    BE_PAID(sngBlock(), "bePaid"),
    STRIPE(natoBlock(), "stripe"),
    BANK_PROCESSING(List.of(), "plain-bank-processing");

    private final List<CountryCode> regionalProcessing;
    private final String serviceName;

    public static PaymentProcessor get(String countryCode) {
        CountryCode code = CountryCode.getCountryCode(countryCode);
        if (code == null) {
            throw new IllegalArgumentException("Country code " + countryCode + " not found");
        }

        return BE_PAID.regionalProcessing.contains(code) ? BE_PAID :
                STRIPE.regionalProcessing.contains(code) ? STRIPE : BANK_PROCESSING;
    }

    private static List<CountryCode> sngBlock() {
        return List.of(
                BELARUS,
                RUSSIA,
                UKRAINE,
                GEORGIA,
                ARMENIA,
                KYRGYZSTAN,
                KAZAKHSTAN,
                TURKMENISTAN,
                TAJIKISTAN
        );
    }

    private static List<CountryCode> natoBlock() {
        return List.of(
                UNITED_STATES,
                CANADA,
                MEXICO,
                AUSTRALIA,
                AUSTRIA,
                BELGIUM,
                BULGARIA,
                CYPRUS,
                CZECH_REPUBLIC,
                DENMARK,
                ESTONIA,
                FINLAND,
                FRANCE,
                GERMANY,
                GREECE,
                HUNGARY,
                IRELAND,
                ITALY,
                LATVIA,
                LITHUANIA,
                LUXEMBOURG,
                MALTA,
                NETHERLANDS,
                NORWAY,
                POLAND,
                PORTUGAL,
                ROMANIA,
                SLOVAKIA,
                SLOVENIA,
                SPAIN,
                SWEDEN,
                SWITZERLAND,
                UNITED_KINGDOM,
                INDIA,
                INDONESIA,
                JAPAN,
                MALAYSIA,
                NEW_ZEALAND,
                SINGAPORE,
                SOUTH_KOREA,
                TAIWAN,
                THAILAND,
                ARGENTINA,
                BRAZIL,
                CHILE,
                COLOMBIA,
                PERU,
                UNITED_ARAB_EMIRATES,
                SAUDI_ARABIA,
                ISRAEL,
                SOUTH_AFRICA
        );
    }
}
