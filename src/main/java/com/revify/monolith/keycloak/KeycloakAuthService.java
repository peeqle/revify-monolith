package com.revify.monolith.keycloak;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.commons.models.auth.KeycloakLoginRequest;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.service.ReadUserService;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAuthService {

    private final KeycloakProvider keycloakProvider;

    private final ReadUserService readUserService;

    private final Gson gson = new GsonBuilder().create();

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

    public TokenResponse refreshToken(String token) throws IOException, InterruptedException {
        String tokenEndpoint = keycloakProvider.buildRefreshRequestURL();
        Tuple2<String, String> tokens = keycloakProvider.realmTokens();

        String formData = "grant_type=refresh_token" +
                "&client_id=" + tokens._1 +
                "&client_secret=" + tokens._2 +
                "&refresh_token=" + token;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenEndpoint))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to refresh token");
        }

        return gson.fromJson(response.body(), TokenResponse.class);
    }
}
