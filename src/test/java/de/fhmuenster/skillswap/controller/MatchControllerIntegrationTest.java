package de.fhmuenster.skillswap.controller;

import de.fhmuenster.skillswap.config.SecurityConfig;
import de.fhmuenster.skillswap.dto.MatchResponseDTO;
import de.fhmuenster.skillswap.service.MatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatchController.class)
@Import(SecurityConfig.class) // Lädt die Keycloak/JWT-Sicherheitskonfiguration
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MatchService matchService;

    @MockitoBean
    private JwtDecoder jwtDecoder; // Wird gemockt, damit Spring Security nicht versucht, den echten Keycloak-Server zu erreichen

    @Test
    void getMatches_ShouldReturnListOfMatches() throws Exception {
        // Arrange
        MatchResponseDTO match1 = new MatchResponseDTO();
        match1.setId(UUID.randomUUID());
        match1.setMatchscore(85);

        when(matchService.getAllMatches()).thenReturn(List.of(match1));

        // Act & Assert
        mockMvc.perform(get("/matches")
                        // simulieren einen eingeloggten User mit JWT
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].matchscore").value(85));
    }

    @Test
    void deleteMatch_WhenMatchExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        UUID matchId = UUID.randomUUID();
        when(matchService.deleteMatch(matchId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/matches/" + matchId)
                        // Für das Löschen simulieren wir einen Admin
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent()); // 204
    }

    @Test
    void deleteMatch_WhenMatchDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        UUID matchId = UUID.randomUUID();
        when(matchService.deleteMatch(matchId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/matches/" + matchId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound()); // 404
    }
}