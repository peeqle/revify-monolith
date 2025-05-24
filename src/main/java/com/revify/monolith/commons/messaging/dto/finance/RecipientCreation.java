package com.revify.monolith.commons.messaging.dto.finance;

import com.revify.monolith.commons.geolocation.CountryCode;
import com.revify.monolith.commons.models.user.UserRole;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.models.user.AppUserOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipientCreation {
    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;

    private Long dobDay;
    private Long dobMonth;
    private Long dobYear;

    private String residenceCountryCode;
    private String countryCode;
    private String city;
    private String street;
    private String region;
    private String postalCode;
    private String apartmentHouse;
    private String currency;

    private UserRole userRole;

    private String ip;
    private String browserAccess;

    public static RecipientCreation from(AppUser appUser) {
        AppUserOptions appUserOptions = appUser.getAppUserOptions();
        return RecipientCreation.builder()
                .userId(appUser.getId())

                .username(appUser.getUsername())
                .email(appUser.getEmail())
                .phone(appUser.getPhoneNumber())

                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())

                .dobDay(10L)
                .dobMonth(2L)
                .dobYear(2000L)

                .residenceCountryCode(appUserOptions.getResidence().getIsoCode())
                .countryCode(CountryCode.getCountryCode(appUserOptions.getAddress().country()).getIsoCode())
                .region(appUserOptions.getAddress().region())
                .city(appUserOptions.getAddress().city())
                .street(appUserOptions.getAddress().street())
                .apartmentHouse(appUserOptions.getAddress().apartment())
                .postalCode(appUserOptions.getAddress().postal())

                .currency(appUserOptions.getPreferedCurrency().getName())
                .userRole(appUser.getClientUserRole())
                .ip(appUser.getSystemInformation().getIp())
                .browserAccess(appUser.getSystemInformation().getBrowserAccess())
                .build();
    }
}
