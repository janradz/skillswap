package de.fhmuenster.skillswap.service;

import de.fhmuenster.skillswap.dto.UserCreateRequestDTO;
import de.fhmuenster.skillswap.dto.UserResponseDTO;
import de.fhmuenster.skillswap.exception.HolidayException;
import de.fhmuenster.skillswap.exception.UserNotFoundException;
import de.fhmuenster.skillswap.model.Role;
import de.fhmuenster.skillswap.model.User;
import de.fhmuenster.skillswap.repository.EnquiryRepository;
import de.fhmuenster.skillswap.repository.OfferRepository;
import de.fhmuenster.skillswap.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private EnquiryRepository enquiryRepository;

    @Mock
    private HolidayService holidayService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User("Informatik", new ArrayList<>(), "TestUser", "test@example.invalid", "hashedPassword123", Role.USER);
        ReflectionTestUtils.setField(testUser, "id", userId);
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        UserCreateRequestDTO request = new UserCreateRequestDTO();
        request.setName("NeuerUser");
        request.setEmail("neu@example.invalid");
        request.setPassword("geheim123");
        request.setStudiengang("BWL");
        request.setVerfuegbareTermine(new ArrayList<>());

        when(userRepository.findUserByName("NeuerUser")).thenReturn(Optional.empty()); // Name noch frei
        when(passwordEncoder.encode("geheim123")).thenReturn("hashedPasswordXYZ"); // Passwort hashen
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedUser, "id", UUID.randomUUID());
            return savedUser;
        });

        // Act
        UserResponseDTO response = userService.registerUser(request);

        // Assert
        assertNotNull(response.getId());
        assertEquals("NeuerUser", response.getName());
        assertEquals("BWL", response.getStudiengang());

        // Verifizieren, dass die Mocks richtig aufgerufen wurden
        verify(userRepository).findUserByName("NeuerUser");
        verify(passwordEncoder).encode("geheim123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_ThrowsExceptionWhenNameAlreadyExists() {
        // Arrange
        UserCreateRequestDTO request = new UserCreateRequestDTO();
        request.setName("TestUser"); // Dieser Name ist bereits vergeben

        when(userRepository.findUserByName("TestUser")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            userService.registerUser(request);
        });

        // Verifizieren, dass danach NICHTS gespeichert wird
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_ThrowsHolidayException() {
        // Arrange
        UserCreateRequestDTO request = new UserCreateRequestDTO();
        request.setName("Urlauber");
        request.setPassword("pass");
        // Ein Termin am Neujahrstag
        LocalDateTime terminAnNeujahr = LocalDateTime.of(2024, 1, 1, 14, 0);
        request.setVerfuegbareTermine(List.of(terminAnNeujahr));

        when(userRepository.findUserByName("Urlauber")).thenReturn(Optional.empty());
        when(holidayService.findHolidayName(LocalDate.of(2024, 1, 1)))
                .thenReturn(Optional.of("Neujahr"));

        // Act & Assert
        assertThrows(HolidayException.class, () -> {
            userService.registerUser(request);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUser_Success_CascadeDeletesOffersAndEnquiries() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        boolean result = userService.deleteUser(userId);

        // Assert
        assertTrue(result);

        // WICHTIG: Die Reihenfolge des Löschens verifizieren!
        verify(offerRepository).deleteByUserId(userId);
        verify(enquiryRepository).deleteByUserId(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void testFindModelById_ThrowsUserNotFoundException() {
        // Arrange
        UUID fakeId = UUID.randomUUID();
        when(userRepository.findById(fakeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.findModelById(fakeId);
        });
    }
}