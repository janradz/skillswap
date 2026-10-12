package de.fhmuenster.skillswap.service;

import de.fhmuenster.skillswap.dto.MatchResponseDTO;
import de.fhmuenster.skillswap.model.*;
import de.fhmuenster.skillswap.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceUnitTest {

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchService matchService;

    private User testUser1;
    private User testUser2;
    private Offer testOffer;
    private Enquiry testEnquiry;
    private Match testMatch;
    private UUID matchId;
    private UUID offerId;
    private UUID enquiryId;

    @BeforeEach
    void setUp() {
        offerId = UUID.randomUUID();
        enquiryId = UUID.randomUUID();
        matchId = UUID.randomUUID();

        // Dummy-User erstellen
        testUser1 = new User("Informatik", new ArrayList<>(), "Anbieter", "anbieter@example.invalid", "hash", Role.USER);
        testUser2 = new User("BWL", new ArrayList<>(), "Sucher", "sucher@example.invalid", "hash", Role.USER);

        // Offer und Enquiry erstellen und IDs setzen
        testOffer = new Offer("Mathe", "Biete Mathe", testUser1);
        ReflectionTestUtils.setField(testOffer, "id", offerId);

        testEnquiry = new Enquiry(testUser2, "Suche Mathe", "Mathe");
        ReflectionTestUtils.setField(testEnquiry, "id", enquiryId);

        // Match erstellen und ID setzen
        testMatch = new Match(testOffer, testEnquiry);
        testMatch.setMatchscore(85);
        testMatch.setVereinbarterTermin(LocalDateTime.of(2025, 1, 1, 10, 0));
        ReflectionTestUtils.setField(testMatch, "id", matchId);
    }

    @Test
    void testGetAllMatches() {
        // Arrange
        when(matchRepository.findAll()).thenReturn(List.of(testMatch));

        // Act
        List<MatchResponseDTO> result = matchService.getAllMatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        MatchResponseDTO dto = result.get(0);
        assertEquals(matchId, dto.getId());
        assertEquals(85, dto.getMatchscore());
        assertEquals(MatchStatus.PENDING, dto.getStatus()); // Wird im Konstruktor von Match automatisch gesetzt
        assertEquals(offerId, dto.getOfferId());
        assertEquals(enquiryId, dto.getEnquiryId());
        assertNotNull(dto.getVereinbarterTermin());

        verify(matchRepository).findAll();
    }

    @Test
    void testCreateAutomaticMatch() {
        // Arrange
        int score = 90;
        LocalDateTime termin = LocalDateTime.of(2025, 12, 24, 15, 0);

        // Wenn das Repository speichert, geben wir das Objekt mit generierter ID zurück
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> {
            Match savedMatch = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedMatch, "id", UUID.randomUUID());
            return savedMatch;
        });

        // Act
        Match result = matchService.createAutomaticMatch(testOffer, testEnquiry, score, termin);

        // Assert
        assertNotNull(result.getId()); // ID wurde durch Repository (Mock) generiert
        assertEquals(90, result.getMatchscore());
        assertEquals(termin, result.getVereinbarterTermin());
        assertEquals(MatchStatus.PENDING, result.getStatus());
        assertEquals(testOffer, result.getOffer());
        assertEquals(testEnquiry, result.getInquiry());

        verify(matchRepository).save(any(Match.class));
    }

    @Test
    void testDeleteMatch_Success() {
        // Arrange
        when(matchRepository.existsById(matchId)).thenReturn(true);

        // Act
        boolean isDeleted = matchService.deleteMatch(matchId);

        // Assert
        assertTrue(isDeleted);
        verify(matchRepository).deleteById(matchId);
    }

    @Test
    void testDeleteMatch_NotFound() {
        // Arrange
        when(matchRepository.existsById(matchId)).thenReturn(false);

        // Act
        boolean isDeleted = matchService.deleteMatch(matchId);

        // Assert
        assertFalse(isDeleted);
        verify(matchRepository, never()).deleteById(any(UUID.class));
    }
}