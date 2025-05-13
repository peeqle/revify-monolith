package com.revify.monolith.user.service.util;


import com.revify.monolith.commons.ValidationContext;
import com.revify.monolith.commons.models.user.UpdateAccountRequest;

import java.util.ArrayList;
import java.util.List;

public class RequestValidator {

    public static List<ValidationContext> validateUpdateRequest(UpdateAccountRequest updateAccountRequest) {
        List<ValidationContext> validationContexts = new ArrayList<>();

        if (updateAccountRequest.getEmail() == null || updateAccountRequest.getEmail().isEmpty()) {
            validationContexts.add(ValidationContext.EMAIL_EMPTY);
        }

        if (updateAccountRequest.getPhoneNumber() == null || updateAccountRequest.getPhoneNumber().isEmpty()) {
            validationContexts.add(ValidationContext.PHONE_EXISTS);
        }

        return validationContexts;
    }
}
