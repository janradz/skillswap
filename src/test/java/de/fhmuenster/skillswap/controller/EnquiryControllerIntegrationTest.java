package de.fhmuenster.skillswap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.fhmuenster.skillswap.config.SecurityConfig;
import de.fhmuenster.skillswap.dto.EnquiryCreateRequestDTO;
import de.fhmuenster.skillswap.dto.EnquiryResponseDTO;
import de.fhmuenster.skillswap.service.EnquiryService;
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

@WebMvcTest(EnquiryController.class)
@Import(SecurityConfig.class)
class EnquiryControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @MockitoBean private EnquiryService enquiryService;
    @MockitoBean private JwtDecoder jwtDecoder;

    @Test
    void getRequests_ShouldReturnList() throws Exception {
        when(enquiryService.getAllEnquiries()).thenReturn(List.of(new EnquiryResponseDTO()));

        mockMvc.perform(get("/enquiries")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void createRequest_ShouldReturnCreated() throws Exception {
        EnquiryCreateRequestDTO request = new EnquiryCreateRequestDTO();
        request.setTitel("Hilfe");
        request.setBeschreibung("Brauche Hilfe");
        request.setUserId(UUID.randomUUID());

        EnquiryResponseDTO response = new EnquiryResponseDTO();
        response.setTitel("Hilfe");

        when(enquiryService.createEnquiry(any())).thenReturn(response);

        mockMvc.perform(post("/enquiries")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titel").value("Hilfe"));
    }
}