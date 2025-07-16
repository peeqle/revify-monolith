package com.revify.monolith.user.service.phone_messaging;

import org.springframework.http.HttpStatus;

public interface CodeVerification {
    HttpStatus checkCodeAndEnable(String phone, String code);
}
