package com.revify.monolith.commons.models.keycloak;

import lombok.Data;

@Data
public class KeycloakUserModel {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String userId;
}
