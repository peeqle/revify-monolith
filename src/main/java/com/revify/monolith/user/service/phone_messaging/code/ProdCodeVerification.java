package com.revify.monolith.user.service.phone_messaging.code;

import com.revify.monolith.user.ActivationSessionHolder;
import com.revify.monolith.user.models.PhoneVerificationCode;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.service.ReadUserService;
import com.revify.monolith.user.service.WriteUserService;
import com.revify.monolith.user.service.phone_messaging.PhoneInteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.revify.monolith.commons.ValidationContext.REGISTRATION_CODE_NOT_VALID;
import static com.revify.monolith.commons.ValidationContext.USER_NOT_PERSIST;

@Slf4j
@Component
@RequiredArgsConstructor

@Profile("!dev")
public class ProdCodeVerification implements CodeVerification {

    private final ReadUserService readUserService;
    private final WriteUserService writeUserService;

    private final PhoneInteractionService phoneInteractionService;

    private final ActivationSessionHolder activationSessionHolder;

    @Override
    public HttpStatus checkCodeAndEnable(String phone, String code) {
        Optional<AppUser> supplier;
        if (phone == null || phone.isBlank()) {
            supplier = readUserService.loadUserByEmail(emailFetcher(activationSessionHolder));
        } else {
            supplier = readUserService.loadUserByPhone(phone);
        }
        PhoneVerificationCode lastUserCode = phoneInteractionService
                .findLastUserCode(supplier.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find specified user")));

        AppUser appUser = supplier.get();
        if (lastUserCode != null) {
            if (lastUserCode.getCode().equals(code)) {
                return writeUserService.enableUser(appUser);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, REGISTRATION_CODE_NOT_VALID.name());
        }
        phoneInteractionService.removeAllForUser(appUser);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_PERSIST.name());
    }
}
