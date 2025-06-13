package com.revify.monolith.user.service.phone_messaging;

import com.revify.monolith.commons.geolocation.CountryCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegionalBeanResolver {

    private final SMSBYMessagingService smsByMessagingService;

    private final VonageMessagingService vonageMessagingService;

    private final MockedMessagingService mockedMessagingService;

    private final Environment environment;


    public PhoneMessagingService getPhoneMessagingService(CountryCode countryCode) {
        if (environment.matchesProfiles("dev")) {
            return mockedMessagingService;
        }

        return switch (countryCode) {
            case BELARUS, KAZAKHSTAN, RUSSIA -> smsByMessagingService;
            default -> vonageMessagingService;
        };
    }
}
