package de.fhmuenster.skillswap.service;

import de.fhmuenster.skillswap.model.Enquiry;
import de.fhmuenster.skillswap.model.Offer;
import de.fhmuenster.skillswap.model.Role;
import de.fhmuenster.skillswap.model.User;
import de.fhmuenster.skillswap.repository.EnquiryRepository;
import de.fhmuenster.skillswap.repository.MatchRepository;
import de.fhmuenster.skillswap.repository.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchEngineServiceUnitTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private EnquiryRepository enquiryRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchService matchService;

    // Wir mocken explizit den Builder und den fertigen ChatClient
    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    // KEIN @InjectMocks mehr, wir machen das manuell!
    private MatchEngineService matchEngineService;

    private User user1;
    private User user2;
    private Offer offer;
    private Enquiry enquiry;

    @BeforeEach
    void setUp() {
        // Dem Mock beibringen: Wenn .build() aufgerufen wird, gib den ChatClient Mock zurück
        when(chatClientBuilder.build()).thenReturn(chatClient);

        // Service manuell instanziieren - das behebt den Fehler "chatClientBuilder is null"
        matchEngineService = new MatchEngineService(
                offerRepository,
                enquiryRepository,
                matchRepository,
                matchService,
                chatClientBuilder
        );

        // Zwei unterschiedliche User vorbereiten
        user1 = new User("Informatik", new ArrayList<>(), "User1", "u1@example.invalid", "hash", Role.USER);
        ReflectionTestUtils.setField(user1, "id", UUID.randomUUID());

        user2 = new User("BWL", new ArrayList<>(), "User2", "u2@example.invalid", "hash", Role.USER);
        ReflectionTestUtils.setField(user2, "id", UUID.randomUUID());

        // Angebot gehört User 1
        offer = new Offer("Mathe 1", "Biete Nachhilfe", user1);
        ReflectionTestUtils.setField(offer, "id", UUID.randomUUID());

        // Gesuch gehört User 2
        enquiry = new Enquiry(user2, "Suche Nachhilfe", "Mathe 1");
        ReflectionTestUtils.setField(enquiry, "id", UUID.randomUUID());
    }

    @Test
    void runMatchingRoutine_ShouldNotMatchSameUser() {
        // Arrange: Wir manipulieren das Gesuch, sodass es auch User 1 gehört
        enquiry.setUser(user1);

        when(offerRepository.findAll()).thenReturn(List.of(offer));
        when(enquiryRepository.findAll()).thenReturn(List.of(enquiry));

        // Act
        matchEngineService.runMatchingRoutine();

        // Assert: Die KI und die Speicher-Methode dürfen nicht aufgerufen werden
        verify(matchService, never()).createAutomaticMatch(any(), any(), anyInt(), any());
    }

    @Test
    void runMatchingRoutine_ShouldNotMatchIfAlreadyExists() {
        // Arrange
        when(offerRepository.findAll()).thenReturn(List.of(offer));
        when(enquiryRepository.findAll()).thenReturn(List.of(enquiry));

        // Simuliere, dass die Datenbank sagt: "Das Match gibt es schon!"
        when(matchRepository.existsByOfferAndEnquiry(offer, enquiry)).thenReturn(true);

        // Act
        matchEngineService.runMatchingRoutine();

        // Assert
        verify(matchService, never()).createAutomaticMatch(any(), any(), anyInt(), any());
    }

    @Test
    void runMatchingRoutine_ShouldNotMatchIfNoCommonDate() {
        // Arrange
        LocalDateTime termin1 = LocalDateTime.of(2025, 5, 1, 10, 0);
        LocalDateTime termin2 = LocalDateTime.of(2025, 5, 2, 10, 0); // Völlig anderer Termin

        user1.setVerfuegbareTermine(List.of(termin1));
        user2.setVerfuegbareTermine(List.of(termin2));

        when(offerRepository.findAll()).thenReturn(List.of(offer));
        when(enquiryRepository.findAll()).thenReturn(List.of(enquiry));
        when(matchRepository.existsByOfferAndEnquiry(offer, enquiry)).thenReturn(false);

        // Act
        matchEngineService.runMatchingRoutine();

        // Assert: Da die Termine nicht übereinstimmen, bricht die Schleife vor der KI ab
        verify(matchService, never()).createAutomaticMatch(any(), any(), anyInt(), any());
    }
}