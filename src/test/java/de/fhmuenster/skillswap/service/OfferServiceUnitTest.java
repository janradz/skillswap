package de.fhmuenster.skillswap.service;

import de.fhmuenster.skillswap.dto.OfferCreateRequestDTO;
import de.fhmuenster.skillswap.dto.OfferResponseDTO;
import de.fhmuenster.skillswap.model.Offer;
import de.fhmuenster.skillswap.model.Role;
import de.fhmuenster.skillswap.model.User;
import de.fhmuenster.skillswap.model.UserBookmarks;
import de.fhmuenster.skillswap.repository.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceUnitTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserBookmarks userBookmarks;

    // InjectMocks packt Mocks in den OfferService
    @InjectMocks
    private OfferService offerService;

    private User testUser;
    private Offer testOffer;
    private UUID userId;
    private UUID offerId;

    @BeforeEach
    void setUp() {
        // Testdaten vorbereiten, die in mehreren Tests verwendet werden
        userId = UUID.randomUUID();
        offerId = UUID.randomUUID();

        testUser = new User("Informatik", new ArrayList<>(), "TestUser", "test@example.invalid", "hash", Role.USER);
        // Da die ID von der Datenbank generiert wird (kein Setter vorhanden), setzen wir sie via Reflection für den Test
        ReflectionTestUtils.setField(testUser, "id", userId);

        testOffer = new Offer("Mathe Nachhilfe", "Biete Nachhilfe in Analysis 1", testUser);
        ReflectionTestUtils.setField(testOffer, "id", offerId);
    }

    @Test
    void testGetAllOffers() {
        // Arrange
        when(offerRepository.findAll()).thenReturn(List.of(testOffer));

        // Act
        List<OfferResponseDTO> result = offerService.getAllOffers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        OfferResponseDTO dto = result.get(0);
        assertEquals(offerId, dto.getId());
        assertEquals("Mathe Nachhilfe", dto.getTitel());
        assertEquals("Biete Nachhilfe in Analysis 1", dto.getBeschreibung());
        assertEquals(userId, dto.getUserId());

        // Verifizieren, dass findAll genau einmal aufgerufen wurde
        verify(offerRepository).findAll();
    }

    @Test
    void testCreateOffer() {
        // Arrange
        OfferCreateRequestDTO requestDTO = new OfferCreateRequestDTO();
        requestDTO.setTitel("Programmieren Java");
        requestDTO.setBeschreibung("Helfe bei Spring Boot");
        requestDTO.setUserId(userId);

        Offer savedOffer = new Offer(requestDTO.getTitel(), requestDTO.getBeschreibung(), testUser);
        ReflectionTestUtils.setField(savedOffer, "id", UUID.randomUUID());

        // Mocks konfigurieren
        when(userService.findModelById(userId)).thenReturn(testUser);
        when(offerRepository.save(any(Offer.class))).thenReturn(savedOffer);

        // Act
        OfferResponseDTO result = offerService.createOffer(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Programmieren Java", result.getTitel());
        assertEquals("Helfe bei Spring Boot", result.getBeschreibung());
        assertEquals(userId, result.getUserId());
        assertNotNull(result.getId());
        assertNotNull(result.getErstelltAm());

        // Verifizieren
        verify(userService).findModelById(userId);
        verify(offerRepository).save(any(Offer.class));
    }

    @Test
    void deleteOffer_ShouldReturnTrue_WhenEnquiryExists() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        when(offerRepository.existsById(mockId)).thenReturn(true);

        // Act
        boolean isDeleted = offerService.deleteOffer(mockId);

        // Assert
        assert(isDeleted);
        verify(offerRepository).deleteById(mockId);
    }

    @Test
    void deleteOffer_ShouldReturnFalse_WhenEnquiryDoesNotExists() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        when(offerRepository.existsById(mockId)).thenReturn(false);

        // Act
        boolean isDeleted = offerService.deleteOffer(mockId);

        // Assert
        assertFalse(isDeleted);
        // Verifiziere, dass deleteById nie aufgerufen wurde, auch mit keiner anderen ID
        verify(offerRepository, never()).deleteById(any(UUID.class));

    }
}