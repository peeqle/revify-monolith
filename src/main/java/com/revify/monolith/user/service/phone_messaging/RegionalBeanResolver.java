package com.revify.monolith.user.service.phone_messaging;

import com.revify.monolith.commons.geolocation.CountryCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegionalBeanResolver {

    private final SMSBYMessagingService smsByMessagingService;

    private final VonageMessagingService vonageMessagingService;

    public PhoneMessagingService getPhoneMessagingService(CountryCode countryCode) {
        return switch (countryCode) {
            case BELARUS, KAZAKHSTAN, RUSSIA -> smsByMessagingService;
            default -> vonageMessagingService;
        };
    }
}
