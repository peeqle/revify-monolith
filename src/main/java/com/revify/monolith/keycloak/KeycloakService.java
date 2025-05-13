package com.revify.monolith.keycloak;

import com.revify.monolith.commons.exceptions.KeycloakUserNotFound;
import com.revify.monolith.commons.models.auth.KeycloakClientRoles;
import com.revify.monolith.commons.models.auth.KeycloakUserAttributes;
import com.revify.monolith.commons.models.user.RegisterRequest;
import com.revify.monolith.config.properties.KeycloakConfigProperties;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakConfigProperties keycloakConfigProperties;

    private final KeycloakProvider keycloakProvider;

    public String registerUser(RegisterRequest userModel) {
        UsersResource usersResource = keycloakProvider.getInstance().realm(keycloakConfigProperties.getRealm()).users();
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(userModel.getPassword());

        UserRepresentation kcUser = getUserRepresentation(userModel, credentialRepresentation);

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

    public boolean deleteUser(String id) {
        UsersResource usersResource = keycloakProvider.getInstance().realm(keycloakConfigProperties.getRealm()).users();
        Response response = usersResource.delete(id);
        if (response.getStatus() >= 300) {
            return false;
        }
        response.close();
        return true;
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

    private UserRepresentation getUserRepresentation(RegisterRequest registerRequest, CredentialRepresentation credentialRepresentation) {
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(registerRequest.getUsername());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(registerRequest.getFirstName());
        kcUser.setLastName(registerRequest.getLastName());
        kcUser.setEmail(registerRequest.getEmail());
        kcUser.setEnabled(false);
        kcUser.setEmailVerified(true);
        kcUser.setAttributes(Map.of(KeycloakUserAttributes.SYSTEM_USER_ID.getKey(), List.of(String.valueOf(registerRequest.getUserId()))));
        kcUser.setClientRoles(
                Map.of(keycloakConfigProperties.getResource(),
                        Collections.singletonList(KeycloakClientRoles.USER.name()))
        );
        return kcUser;
    }
}
