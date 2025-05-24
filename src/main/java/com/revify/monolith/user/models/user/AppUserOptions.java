package com.revify.monolith.user.models.user;

import com.revify.monolith.commons.finance.Currency;
import com.revify.monolith.commons.geolocation.CountryCode;
import com.revify.monolith.user.models.user.additional.Locale;
import com.revify.monolith.user.models.user.additional.rating.UserRating;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
public class AppUserOptions implements Serializable {

    private ZoneId zoneId = ZoneOffset.UTC;

    private Locale locale = Locale.ENGLISH;

    private CountryCode residence;

    private UserRating userRating;

    private Currency preferedCurrency = Currency.USD;
}
