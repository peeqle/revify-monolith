package com.revify.monolith.commons.models.auth;

import lombok.Data;

@Data
public class AccessTokenResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private Long refreshTokenExpiresIn;
    private String scope;
}
