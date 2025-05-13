package com.revify.monolith.keycloak;

import com.revify.monolith.config.properties.KeycloakConfigProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class KeycloakProvider {

    private final KeycloakConfigProperties keycloakConfigProperties;

    private static Keycloak keycloakInstance = null;

    public Keycloak getInstance() {
        if (keycloakInstance == null) {
            keycloakInstance = KeycloakBuilder.builder()
                    .realm(keycloakConfigProperties.getRealm())
                    .username(keycloakConfigProperties.getUsername())
                    .password(keycloakConfigProperties.getPassword())
                    .serverUrl(keycloakConfigProperties.getAuthServerUrl())
                    .clientId(keycloakConfigProperties.getResource())
                    .clientSecret(String.valueOf(keycloakConfigProperties.getCredentials().getSecret()))
                    .grantType(OAuth2Constants.PASSWORD)
                    .build();
        }
        return keycloakInstance;
    }

    public Keycloak createSessionInstance(String username, String password) {
        return KeycloakBuilder.builder()
                .realm(keycloakConfigProperties.getRealm())
                .username(username)
                .password(password)
                .serverUrl(keycloakConfigProperties.getAuthServerUrl())
                .clientId(keycloakConfigProperties.getResource())
                .clientSecret(String.valueOf(keycloakConfigProperties.getCredentials().getSecret()))
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }
}