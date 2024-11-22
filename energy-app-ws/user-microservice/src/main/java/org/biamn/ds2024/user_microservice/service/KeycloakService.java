package org.biamn.ds2024.user_microservice.service;

import jakarta.annotation.PostConstruct;

import jakarta.ws.rs.core.Response;
import org.biamn.ds2024.user_microservice.dto.user.UserRequestDTO;
import org.biamn.ds2024.user_microservice.dto.user.UserResponseDTO;
import org.biamn.ds2024.user_microservice.model.Role;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;


@Service
public class KeycloakService {

    private Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.grant-type}")
    private String grantType;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

//    static class KeycloakConfig implements ClientRequestFilter {
//        @Override
//        public void filter(ClientRequestContext requestContext) {
//            requestContext.getHeaders().putSingle("Host", "keycloak.localhost");
//        }
//    }

    @PostConstruct
    public void init() {
//        ResteasyClient resteasyClient = new ResteasyClientBuilderImpl()
//                .register(new KeycloakConfig())
//                .build();

        keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm) // Using the master realm or the realm with admin permissions
                .clientId(clientId) // Using the admin client ID
                .username(adminUsername) // Admin username
                .password(adminPassword) // Admin password
                .grantType(grantType) // Using the password grant type
//                .resteasyClient(resteasyClient)
                .build();

        System.out.println("Keycloak service initialized");
    }

    public String getToken() {
        return keycloak.tokenManager().getAccessTokenString();
    }

    public UUID createUser(UserRequestDTO userRequestDTO) {
        RealmResource realmResource = keycloak.realm(this.realm);

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userRequestDTO.getEmail());
        user.setEmail(userRequestDTO.getEmail());
        user.setEmailVerified(true);
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(userRequestDTO.getPassword());
        passwordCred.setTemporary(false);
        user.setCredentials(Collections.singletonList(passwordCred));

        Response response = realmResource.users().create(user);
        if (response.getStatus() == 201) {
            String location = response.getLocation().toString();
            String createdUserId = location.substring(location.lastIndexOf("/") + 1);
            RoleRepresentation role = keycloak.realm(realm).roles().get(userRequestDTO.getRole().name()).toRepresentation();
            keycloak.realm(realm).users().get(createdUserId).roles().realmLevel().add(Collections.singletonList(role));
            return UUID.fromString(createdUserId);
        } else {
            String errorMessage = response.readEntity(String.class);
            System.out.println("Failed to create user: " + response.getStatus() + " - " + errorMessage);
            throw new RuntimeException("Failed to create user: " + response.getStatus() + " - " + errorMessage);
        }
    }

    // Update User
    public UserResponseDTO updateUser(UUID userId, UserRequestDTO userRequestDTO) {
        RealmResource realmResource = keycloak.realm(realm);
        UserResource userResource = realmResource.users().get(userId.toString());
        UserRepresentation user = userResource.toRepresentation();
        user.setUsername(userRequestDTO.getEmail());
        user.setEmail(userRequestDTO.getEmail());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());

        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(userRequestDTO.getPassword());
            passwordCred.setTemporary(false);
            user.setCredentials(Collections.singletonList(passwordCred));
        }

        user.setRealmRoles(Collections.singletonList(userRequestDTO.getRole().name()));

        try {
            userResource.update(user);
            return new UserResponseDTO(UUID.fromString(user.getId()), userRequestDTO.getFirstName(),
                    userRequestDTO.getLastName(), userRequestDTO.getEmail(),  userRequestDTO.getPassword(), userRequestDTO.getRole());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage());
        }
    }

    // Delete User
    public void deletById(UUID userId) {
        RealmResource realmResource = keycloak.realm(realm);
        UserResource userResource = realmResource.users().get(userId.toString());

        try {
            userResource.remove();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }

    public List<UserResponseDTO> listUsers() {
        RealmResource realmResource = keycloak.realm(realm);
        List<UserRepresentation> users = realmResource.users().list();

        return users.stream().map(user -> new UserResponseDTO(
                UUID.fromString(user.getId()),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCredentials().get(0).getValue(),
                user.getRealmRoles().stream().map(Role::valueOf).findFirst().orElse(null)
        )).collect(Collectors.toList());
    }


}
