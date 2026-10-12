package de.fhmuenster.skillswap.service;

import de.fhmuenster.skillswap.dto.EnquiryCreateRequestDTO;
import de.fhmuenster.skillswap.dto.EnquiryResponseDTO;
import de.fhmuenster.skillswap.model.Enquiry;
import de.fhmuenster.skillswap.model.Role;
import de.fhmuenster.skillswap.model.User;
import de.fhmuenster.skillswap.repository.EnquiryRepository;
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
class EnquiryServiceUnitTest {

    @Mock
    private EnquiryRepository enquiryRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private EnquiryService enquiryService;

    private User testUser;
    private Enquiry testEnquiry;
    private UUID userId;
    private UUID enquiryId;

    @BeforeEach
    void setUp() {
        // Testdaten vorbereiten, die in mehreren Tests verwendet werden
        userId = UUID.randomUUID();
        enquiryId = UUID.randomUUID();

        testUser = new User("Informatik", new ArrayList<>(), "TestUser", "test@example.invalid", "hash", Role.USER);
        ReflectionTestUtils.setField(testUser, "id", userId);

        testEnquiry = new Enquiry(testUser, "Suche Nachhilfe in Analysis 1", "Mathe Nachhilfe");
        ReflectionTestUtils.setField(testEnquiry, "id", enquiryId); // Umbenannt
    }

    @Test
    void testGetAllEnquiries() {
        // Arrange
        when(enquiryRepository.findAll()).thenReturn(List.of(testEnquiry));

        // Act
        List<EnquiryResponseDTO> result = enquiryService.getAllEnquiries();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        EnquiryResponseDTO dto = result.get(0);
        assertEquals(enquiryId, dto.getId()); // Umbenannt
        assertEquals("Mathe Nachhilfe", dto.getTitel());
        assertEquals("Suche Nachhilfe in Analysis 1", dto.getBeschreibung());
        assertEquals(userId, dto.getUserId());

        // Verifizieren, dass findAll genau einmal aufgerufen wurde
        verify(enquiryRepository).findAll();
    }

    @Test
    void testCreateEnquiry() { // Umbenannt von testCreateOffer
        // Arrange
        EnquiryCreateRequestDTO requestDTO = new EnquiryCreateRequestDTO();
        requestDTO.setTitel("Programmieren Java");
        requestDTO.setBeschreibung("Helfe bei Spring Boot");
        requestDTO.setUserId(userId);

        Enquiry savedEnquiry = new Enquiry(testUser, requestDTO.getBeschreibung(), requestDTO.getTitel());
        ReflectionTestUtils.setField(savedEnquiry, "id", UUID.randomUUID());

        // Mocks konfigurieren
        when(userService.findModelById(userId)).thenReturn(testUser);
        when(enquiryRepository.save(any(Enquiry.class))).thenReturn(savedEnquiry);

        // Act
        EnquiryResponseDTO result = enquiryService.createEnquiry(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Programmieren Java", result.getTitel());
        assertEquals("Helfe bei Spring Boot", result.getBeschreibung());
        assertEquals(userId, result.getUserId());
        assertNotNull(result.getId());
        assertNotNull(result.getErstelltAm());

        // Verifizieren
        verify(userService).findModelById(userId);
        verify(enquiryRepository).save(any(Enquiry.class));
    }

    @Test
    void deleteEnquiry_ShouldReturnTrue_WhenEnquiryExists() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        when(enquiryRepository.existsById(mockId)).thenReturn(true);

        // Act
        boolean isDeleted = enquiryService.deleteEnquiry(mockId);

        // Assert
        assertTrue(isDeleted);
        verify(enquiryRepository).deleteById(mockId);
    }

    @Test
    void deleteEnquiry_ShouldReturnFalse_WhenEnquiryDoesNotExist() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        when(enquiryRepository.existsById(mockId)).thenReturn(false);

        // Act
        boolean isDeleted = enquiryService.deleteEnquiry(mockId);

        // Assert
        assertFalse(isDeleted);
        verify(enquiryRepository, never()).deleteById(any());
    }
}