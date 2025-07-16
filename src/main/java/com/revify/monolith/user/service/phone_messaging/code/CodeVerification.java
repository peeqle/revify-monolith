package com.revify.monolith.user.service.phone_messaging.code;

import com.revify.monolith.user.ActivationSessionHolder;
import org.springframework.http.HttpStatus;

public interface CodeVerification {
    HttpStatus checkCodeAndEnable(String phone, String code);

    default String emailFetcher(ActivationSessionHolder activationSessionHolder) {
        String email = activationSessionHolder.getEmail();
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Cannot verify current user session state");
        }

        return email;
    }
}
