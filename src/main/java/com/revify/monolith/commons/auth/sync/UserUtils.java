package com.revify.monolith.commons.auth.sync;

import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import static com.revify.monolith.commons.auth.ClaimNames.*;


public class UserUtils {

    public static String getKeycloakId() throws UnauthorizedAccessError {
        Authentication context = getContext();
        return context.getName();
    }

    public static long getUserId() throws UnauthorizedAccessError {
        Jwt principal = getPrincipal();
        return Long.parseLong(principal.getClaimAsString(USER_ID));
    }

    public static String getUsername() throws UnauthorizedAccessError {
        Jwt principal = getPrincipal();
        return principal.getClaimAsString(USERNAME);
    }

    public static String getName() throws UnauthorizedAccessError {
        Jwt principal = getPrincipal();
        return principal.getClaimAsString(NAME);
    }

    public static String getEmail() throws UnauthorizedAccessError {
        Jwt principal = getPrincipal();
        return principal.getClaimAsString(EMAIL);
    }

    private static Jwt getPrincipal() throws UnauthorizedAccessError {
        Authentication auth = getContext();
        Jwt principal = (Jwt) auth.getPrincipal();
        if (principal != null && principal.getClaims() != null) {
            return principal;
        }

        throw new UnauthorizedAccessError();
    }

    private static Authentication getContext() throws UnauthorizedAccessError {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated()) {
            return authentication;
        }
        throw new UnauthorizedAccessError();
    }
}
