package com.revify.monolith.commons.auth.async;

import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static com.revify.monolith.commons.auth.ClaimNames.*;


public class UserUtils {

    public static Mono<String> getKeycloakId() {
        return getContext().map(Principal::getName);
    }

    public static Mono<Long> getUserId() {
        return getPrincipal().map(e -> Long.parseLong(e.getClaimAsString(USER_ID)));
    }

    public static Mono<String> getUsername() {
        return getPrincipal().map(e -> e.getClaimAsString(USERNAME));
    }

    public static Mono<String> getName() {
        return getPrincipal().map(e -> e.getClaimAsString(NAME));
    }

    public static Mono<String> getEmail() {
        return getPrincipal().map(e -> e.getClaimAsString(EMAIL));
    }

    private static Mono<Jwt> getPrincipal() {
        Mono<Authentication> auth = getContext();
        return auth.handle((sec, sink) -> {
            Jwt principal = (Jwt) sec.getPrincipal();
            if (principal != null && principal.getClaims() != null) {
                sink.next(principal);
                return;
            }
            sink.error(new UnauthorizedAccessError());
        });
    }

    private static Mono<Authentication> getContext() {
        return ReactiveSecurityContextHolder.getContext()
                .handle((securityContext, sink) -> {
                    if (securityContext.getAuthentication().isAuthenticated()) {
                        sink.next(securityContext.getAuthentication());
                        return;
                    }
                    sink.error(new UnauthorizedAccessError());
                });
    }
}
