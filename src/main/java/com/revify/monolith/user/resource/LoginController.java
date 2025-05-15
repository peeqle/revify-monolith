package com.revify.monolith.user.resource;

import com.revify.monolith.commons.models.auth.AccessTokenResponse;
import com.revify.monolith.commons.models.auth.KeycloakLoginRequest;
import com.revify.monolith.keycloak.KeycloakAuthService;
import com.revify.monolith.keycloak.TokenResponse;
import com.revify.monolith.user.service.ReadUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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

        if (!readUserService.isUserActivated(keycloakLoginRequest.email())) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body("User is not activated");
        }

        var tokens = keycloakAuthService.login(keycloakLoginRequest);

        if (tokens != null) {
            return ResponseEntity.ok(createAccessTokenResponse(tokens));
        }
        return ResponseEntity.status(NOT_ACCEPTABLE).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            TokenResponse tokens = keycloakAuthService.refreshToken(request.refreshToken);
            if (tokens != null) {
                return ResponseEntity.ok(createAccessTokenResponse(tokens));
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.status(NOT_ACCEPTABLE).build();
    }

    public static AccessTokenResponse createAccessTokenResponse(TokenResponse tokenResponse) {
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();

        accessTokenResponse.setAccessToken(tokenResponse.accessToken());
        accessTokenResponse.setRefreshToken(tokenResponse.refreshToken());
        accessTokenResponse.setExpiresIn(tokenResponse.expiresIn());
        accessTokenResponse.setRefreshTokenExpiresIn(tokenResponse.rExpiresIn());
        accessTokenResponse.setScope(tokenResponse.scope());

        return accessTokenResponse;
    }

    private record RefreshTokenRequest(String refreshToken) {
    }
}
