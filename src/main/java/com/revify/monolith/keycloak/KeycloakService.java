package com.revify.monolith.keycloak;

import com.revify.monolith.commons.exceptions.KeycloakUserNotFound;
import com.revify.monolith.commons.models.auth.KeycloakClientRoles;
import com.revify.monolith.commons.models.auth.KeycloakUserAttributes;
import com.revify.monolith.config.properties.KeycloakConfigProperties;
import com.revify.monolith.user.models.user.AppUser;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.RSATokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.common.VerificationException;
import org.keycloak.jose.jwk.JWK;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.KeysMetadataRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakConfigProperties keycloakConfigProperties;

    private final KeycloakProvider keycloakProvider;

    public String registerUser(AppUser newAppUser, String password) {
        UsersResource usersResource = keycloakProvider.getInstance().realm(keycloakConfigProperties.getRealm()).users();
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(password);

        UserRepresentation kcUser = getUserRepresentation(newAppUser, credentialRepresentation);

        Response response = usersResource.create(kcUser);
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            String locationHeader = response.getHeaderString("Location");
            if (locationHeader != null) {
                String userId = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
                response.close();
                return userId;
            }
        }
        return null;
    }

    public void deleteUser(Long id) {
        UsersResource users = keycloakProvider.getInstance()
                .realm(keycloakConfigProperties.getRealm()).users();
        List<UserRepresentation> usersResource = users
                .searchByAttributes(KeycloakUserAttributes.SYSTEM_USER_ID + ":" + id);
        try {
            for (UserRepresentation user : usersResource) {
                Response delete = users.delete(user.getId());
                delete.close();
            }
        } catch (Exception e) {
            log.warn("Error deleting user", e);
        }
    }

    public boolean deleteUser(String email, String username) {
        UsersResource usersResource = keycloakProvider.getInstance().realm(keycloakConfigProperties.getRealm()).users();
        List<UserRepresentation> userRepresentations = usersResource.searchByEmail(email, true);

        if (userRepresentations.isEmpty()) {
            userRepresentations.addAll(usersResource.searchByUsername(username, true));
        }

        for (UserRepresentation representation : userRepresentations) {
            Response response = usersResource.delete(representation.getId());
            if (response.getStatus() >= 300) {
                return false;
            }
            response.close();
        }
        return true;
    }

    public void changeUserAvailability(String username, Boolean value) {
        UserRepresentation userByUsername = findUserByUsername(username);

        Keycloak keycloak = keycloakProvider.getInstance();
        UserResource userResource = keycloak.realm(keycloakConfigProperties.getRealm()).users().get(userByUsername.getId());
        if (userResource != null) {
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(value);
            userResource.update(user);
        } else {
            throw new KeycloakUserNotFound();
        }
    }

    public UserRepresentation findUserByUsername(String username) {
        Keycloak keycloak = keycloakProvider.getInstance();
        List<UserRepresentation> users = keycloak.realm(keycloakConfigProperties.getRealm()).users().search(username);
        if (users.isEmpty()) {
            throw new KeycloakUserNotFound();
        }
        return users.get(0);
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    private UserRepresentation getUserRepresentation(AppUser appUser, CredentialRepresentation credentialRepresentation) {
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(appUser.getUsername());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(appUser.getFirstName());
        kcUser.setLastName(appUser.getLastName());
        kcUser.setEmail(appUser.getEmail());
        kcUser.setEnabled(false);
        kcUser.setEmailVerified(true);
        kcUser.setAttributes(Map.of(KeycloakUserAttributes.SYSTEM_USER_ID.getKey(), List.of(String.valueOf(appUser.getId()))));
        kcUser.setClientRoles(
                Map.of(keycloakConfigProperties.getResource(),
                        Collections.singletonList(KeycloakClientRoles.USER.name()))
        );
        return kcUser;
    }
}
