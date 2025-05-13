package com.revify.monolith.user.service.util;

import com.revify.monolith.commons.ValidationContext;
import com.revify.monolith.commons.models.user.RegisterRequest;
import com.revify.monolith.user.service.ReadUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final ReadUserService readUserService;

    public List<ValidationContext> validateRequest(RegisterRequest registerRequest) {
        List<ValidationContext> validationContextList = new ArrayList<>();
        if (readUserService.phoneExists(registerRequest.getPhoneNumber())) {
            validationContextList.add(ValidationContext.PHONE_EXISTS);
        }
        if (readUserService.emailExists(registerRequest.getEmail())) {
            validationContextList.add(ValidationContext.EMAIL_EXISTS);
        }
        if (readUserService.usernameExists(registerRequest.getUsername())) {
            validationContextList.add(ValidationContext.USERNAME_EXISTS);
        }
        return validationContextList;
    }

}
