package de.fhmuenster.skillswap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.fhmuenster.skillswap.config.SecurityConfig;
import de.fhmuenster.skillswap.dto.UserCreateRequestDTO;
import de.fhmuenster.skillswap.dto.UserResponseDTO;
import de.fhmuenster.skillswap.service.HolidayService;
import de.fhmuenster.skillswap.service.UserService;
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

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean private UserService userService;
    @MockitoBean private HolidayService holidayService; // Wichtig, da im Controller injiziert
    @MockitoBean private JwtDecoder jwtDecoder;

    @Test
    void getUsers_WithJwt_ShouldReturnUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(new UserResponseDTO()));

        mockMvc.perform(get("/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void registerUser_WithoutAuth_ShouldReturnCreated() throws Exception {
        UserCreateRequestDTO request = new UserCreateRequestDTO();
        request.setName("Test");
        request.setEmail("test@example.invalid");
        request.setStudiengang("Info");
        request.setPassword("geheim");

        UserResponseDTO response = new UserResponseDTO();
        response.setName("Test");

        when(userService.registerUser(any())).thenReturn(response);

        // Kein .with(jwt()) da permitAll() in der SecurityConfig!
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void deleteUser_WhenExists_ShouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.deleteUser(id)).thenReturn(true);

        mockMvc.perform(delete("/users/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());
    }
}