package com.revify.monolith.user.service.phone_messaging.code;

import com.revify.monolith.user.ActivationSessionHolder;
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

    private final ActivationSessionHolder activationSessionHolder;

    @Override
    public HttpStatus checkCodeAndEnable(String phone, String code) {
        Optional<AppUser> supplier;
        if (phone == null || phone.isBlank()) {
            supplier = readUserService.loadUserByEmail(emailFetcher(activationSessionHolder));
        } else {
            supplier = readUserService.loadUserByPhone(phone);
        }

        if (supplier.isEmpty()) {
            throw new RuntimeException("No bitches");
        }

        return writeUserService.enableUser(supplier.get());
    }
}
