package de.fhmuenster.skillswap.security;

import de.fhmuenster.skillswap.model.Role;
import de.fhmuenster.skillswap.model.User;
import de.fhmuenster.skillswap.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.ArrayList;

@Component
public class KeycloakUserSyncListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UserRepository userRepository;

    public KeycloakUserSyncListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        if (event.getAuthentication() instanceof JwtAuthenticationToken jwtToken) {
            Jwt jwt = jwtToken.getToken();

            // Lese Daten aus dem JWT
            String subId = jwt.getSubject();
            String email = jwt.getClaimAsString("email");
            String name = jwt.getClaimAsString("preferred_username");
            
            if (subId == null || email == null || name == null) {
                return; // Kein valides Keycloak Token (z.B. Service Account)
            }

            UUID userId;
            try {
                userId = UUID.fromString(subId);
            } catch (IllegalArgumentException e) {
                return; // Keine gültige UUID
            }

            // Prüfe auf Rollen
            Role role = Role.USER;
            if (jwtToken.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                role = Role.ADMIN;
            }
            final Role finalRole = role;

            // Sync mit lokaler DB
            userRepository.findById(userId).ifPresentOrElse(
                    existingUser -> {
                        // User existiert, aktualisiere Daten falls nötig
                        boolean changed = false;
                        if (!existingUser.getName().equals(name)) {
                            existingUser.setName(name);
                            changed = true;
                        }
                        if (!existingUser.getEmail().equals(email)) {
                            existingUser.setEmail(email);
                            changed = true;
                        }
                        if (existingUser.getRole() != finalRole) {
                            existingUser.setRole(finalRole);
                            changed = true;
                        }
                        if (changed) {
                            userRepository.save(existingUser);
                        }
                    },
                    () -> {
                        // Neuer User aus Keycloak -> in DB anlegen
                        User newUser = new User(
                                "N/A", // Default Studiengang
                                new ArrayList<>(), // Keine Termine
                                name,
                                email,
                                "KEYCLOAK_MANAGED", // Kein Passwort-Hash, da SSO
                                finalRole
                        );
                        newUser.setId(userId);
                        userRepository.save(newUser);
                        System.out.println("Neuer Keycloak-User in lokaler Datenbank synchronisiert: " + name);
                    }
            );
        }
    }
}
