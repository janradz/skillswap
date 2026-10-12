package de.fhmuenster.skillswap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.fhmuenster.skillswap.config.SecurityConfig;
import de.fhmuenster.skillswap.dto.OfferCreateRequestDTO;
import de.fhmuenster.skillswap.dto.OfferResponseDTO;
import de.fhmuenster.skillswap.service.OfferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfferController.class)
@Import(SecurityConfig.class)
class OfferControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean private OfferService offerService;
    @MockitoBean private JwtDecoder jwtDecoder;

    @Test
    void getOffers_AsUser_ShouldReturnAllOffers() throws Exception {
        List<OfferResponseDTO> mockOffers = List.of(new OfferResponseDTO(), new OfferResponseDTO(), new OfferResponseDTO(), new OfferResponseDTO());
        when(offerService.getAllOffers()).thenReturn(mockOffers);

        mockMvc.perform(get("/offers")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void getOffers_AsGuest_ShouldReturnMaxThreeOffers() throws Exception {
        List<OfferResponseDTO> mockOffers = List.of(new OfferResponseDTO(), new OfferResponseDTO(), new OfferResponseDTO(), new OfferResponseDTO());
        when(offerService.getAllOffers()).thenReturn(mockOffers);

        // JWT mit ROLE_GUEST simulieren
        mockMvc.perform(get("/offers")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GUEST"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void createOffer_ShouldReturnCreatedOffer() throws Exception {
        OfferCreateRequestDTO request = new OfferCreateRequestDTO();
        request.setTitel("Mathe");
        request.setBeschreibung("Biete Mathe");
        request.setUserId(UUID.randomUUID());

        OfferResponseDTO response = new OfferResponseDTO();
        response.setTitel("Mathe");

        when(offerService.createOffer(any())).thenReturn(response);

        mockMvc.perform(post("/offers")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titel").value("Mathe"));
    }

    @Test
    void deleteOffer_WhenExists_ShouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        when(offerService.deleteOffer(id)).thenReturn(true);

        mockMvc.perform(delete("/offers/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteOffer_WhenNotExists_ShouldReturnNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(offerService.deleteOffer(id)).thenReturn(false);

        mockMvc.perform(delete("/offers/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound());
    }
}