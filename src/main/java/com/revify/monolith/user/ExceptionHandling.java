package com.revify.monolith.user;

import com.revify.monolith.commons.exceptions.UserSessionException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
public class ExceptionHandling {

    @ExceptionHandler(UserSessionException.class)
    public void onUserSessionException() {
        SecurityContextHolder.clearContext();
    }
}
