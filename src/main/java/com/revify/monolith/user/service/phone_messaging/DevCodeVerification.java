package com.revify.monolith.user.service.phone_messaging;

import com.revify.monolith.keycloak.KeycloakService;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.service.ReadUserService;
import com.revify.monolith.user.service.WriteUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor

@Profile("dev")
public class DevCodeVerification implements CodeVerification {

    private final ReadUserService readUserService;
    private final WriteUserService writeUserService;

    @Override
    public HttpStatus checkCodeAndEnable(String phone, String code) {
        Optional<AppUser> appUserOpt = readUserService.loadUserByPhone(phone);
        if (appUserOpt.isEmpty()) {
            throw new RuntimeException("No bitches");
        }

        return writeUserService.enableUser(appUserOpt.get());
    }
}
