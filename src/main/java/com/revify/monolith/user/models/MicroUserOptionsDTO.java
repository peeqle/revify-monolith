package com.revify.monolith.user.models;

import com.revify.monolith.commons.finance.Currency;
import com.revify.monolith.user.models.user.AppUserOptions;
import com.revify.monolith.user.models.user.additional.Locale;
import com.revify.monolith.user.models.user.additional.rating.UserRating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.ZoneId;
import java.time.ZoneOffset;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MicroUserOptionsDTO {
    private ZoneId zoneId = ZoneOffset.UTC;

    private Locale locale = Locale.ENGLISH;

    private UserRating userRating;

    private Currency preferedCurrency = Currency.USD;

    public static MicroUserOptionsDTO from(AppUserOptions options) {
        return MicroUserOptionsDTO.builder()
                .zoneId(options.getZoneId())
                .locale(options.getLocale())
                .userRating(options.getUserRating())
                .preferedCurrency(options.getPreferedCurrency())
                .build();
    }
}
