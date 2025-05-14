package com.revify.monolith.keycloak;

import com.revify.monolith.commons.models.auth.KeycloakLoginRequest;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.service.ReadUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAuthService {

    private final KeycloakProvider keycloakProvider;

    private final ReadUserService readUserService;

    public TokenResponse login(KeycloakLoginRequest keycloakLoginRequest) {
        AppUser appUser = readUserService.loadUserByEmail(keycloakLoginRequest.email());
        return login(appUser.getUsername(), keycloakLoginRequest.password());
    }

    public TokenResponse login(String username, String password) {
        try {
            try (Keycloak sessionInstance = keycloakProvider.createSessionInstance(username, password)) {
                AccessTokenResponse accessTokenResponse = sessionInstance.tokenManager().getAccessToken();
                return new TokenResponse(accessTokenResponse.getToken(), accessTokenResponse.getRefreshToken(),
                        accessTokenResponse.getExpiresIn(), accessTokenResponse.getRefreshExpiresIn(),
                        accessTokenResponse.getScope());

            }
        } catch (Exception e) {
            log.warn("Failed to login", e);
            return null;
        }
    }
}
