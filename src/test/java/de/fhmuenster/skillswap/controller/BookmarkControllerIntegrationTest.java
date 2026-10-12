package de.fhmuenster.skillswap.controller;

import de.fhmuenster.skillswap.config.SecurityConfig;
import de.fhmuenster.skillswap.dto.BookmarkResponseDTO;
import de.fhmuenster.skillswap.service.BookmarkService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookmarkController.class)
@Import(SecurityConfig.class)
class BookmarkControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private BookmarkService bookmarkService;
    @MockitoBean private JwtDecoder jwtDecoder;

    @Test
    void addOfferBookmark_ShouldReturnCreated() throws Exception {
        when(bookmarkService.addOfferToBookmarks(any())).thenReturn(List.of(new BookmarkResponseDTO()));

        mockMvc.perform(post("/bookmarks/offers/" + UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isCreated());
    }
}