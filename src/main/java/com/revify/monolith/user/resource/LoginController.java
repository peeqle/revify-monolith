package com.revify.monolith.user.resource;

import com.revify.monolith.commons.models.auth.AccessTokenResponse;
import com.revify.monolith.commons.models.auth.KeycloakLoginRequest;
import com.revify.monolith.keycloak.KeycloakAuthService;
import com.revify.monolith.user.service.ReadUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final ReadUserService readUserService;

    private final KeycloakAuthService keycloakAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody KeycloakLoginRequest keycloakLoginRequest) {
        if (keycloakLoginRequest == null) {
            return ResponseEntity.status(400).body("Login request body is empty");
        }

        if (!readUserService.isUserActivated(keycloakLoginRequest.username())) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body("User is not activated");
        }

        var login = keycloakAuthService.login(keycloakLoginRequest);

        if (login != null) {
            AccessTokenResponse accessTokenResponse = new AccessTokenResponse();

            accessTokenResponse.setAccessToken(login.accessToken());
            accessTokenResponse.setRefreshToken(login.refreshToken());
            accessTokenResponse.setExpiresIn(login.expiresIn());
            accessTokenResponse.setRefreshTokenExpiresIn(login.rExpiresIn());
            accessTokenResponse.setScope(login.scope());

            return ResponseEntity.ok(accessTokenResponse);
        }
        return ResponseEntity.status(NOT_ACCEPTABLE).build();
    }
}
