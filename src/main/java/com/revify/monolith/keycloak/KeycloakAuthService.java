package com.revify.monolith.keycloak;

import com.revify.monolith.commons.exceptions.KeycloakUserNotFound;
import com.revify.monolith.commons.models.auth.KeycloakLoginRequest;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeycloakAuthService {

    private final KeycloakProvider keycloakProvider;

    public TokenResponse login(KeycloakLoginRequest keycloakLoginRequest) {
        return login(keycloakLoginRequest.username(), keycloakLoginRequest.password());
    }

    public TokenResponse login(String username, String password) {
        try {
            try (Keycloak sessionInstance = keycloakProvider.createSessionInstance(username, password)) {
                AccessTokenResponse accessTokenResponse = sessionInstance.tokenManager().getAccessToken();
                return new TokenResponse(accessTokenResponse.getToken(), accessTokenResponse.getRefreshToken(),
                        accessTokenResponse.getExpiresIn(), accessTokenResponse.getRefreshExpiresIn(),
                        accessTokenResponse.getScope());

            }
        } catch (KeycloakUserNotFound e) {
            return null;
        }
    }
}
