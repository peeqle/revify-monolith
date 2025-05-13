package com.revify.monolith.commons.models.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KeycloakUserAttributes {
    SYSTEM_USER_ID("system_user_id");

    private final String key;
}
