package com.revify.monolith.keycloak;

public record TokenResponse(String accessToken, String refreshToken, Long expiresIn, Long rExpiresIn, String scope) {
}
