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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final EnquiryRepository enquiryRepository;
    private final HolidayService holidayService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, OfferRepository offerRepository, EnquiryRepository enquiryRepository, HolidayService holidayService, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.offerRepository = offerRepository;
        this.enquiryRepository = enquiryRepository;
        this.holidayService = holidayService;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * FÜR DEN CONTROLLER (Außenwelt):
     * Sucht den User und wandelt ihn in ein DTO um.
     */
    @PostAuthorize("hasRole('ADMIN') or returnObject.name == authentication.name")
    public UserResponseDTO getUserById(UUID id) {
        User user = findModelById(id);
        return mapToResponse(user);
    }

    @Transactional // Wichtig für kaskadierendes Löschen über eigene Repository-Methoden
    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            return false;
        }

        // Erst alle Angebote und Gesuche des Users löschen
        offerRepository.deleteByUserId(id);
        enquiryRepository.deleteByUserId(id);

        // Danach den User selbst löschen
        userRepository.deleteById(id);
        return true;
    }

    /**
     * Interne Methode:
     * Sucht das echte User-Modell aus dem Repository inklusive Fehlerbehandlung.
     * Wird z. B. vom OfferService und RequestService genutzt.
     */
    public User findModelById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public UserResponseDTO registerUser(UserCreateRequestDTO registrationDto) {
        // Prüfen, ob der Nutzername schon existiert
        if (userRepository.findUserByName(registrationDto.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Der Benutzername ist bereits vergeben.");
        }

        if (registrationDto.getVerfuegbareTermine() != null) {
            for (LocalDateTime termin : registrationDto.getVerfuegbareTermine()) {
                Optional<String> holidayName = holidayService.findHolidayName(termin.toLocalDate());
                if (holidayName.isPresent()) {
                    throw new HolidayException(termin.toLocalDate(), holidayName.get());
                }
            }
        }

        // Passwort hashen
        String hashedPassword = passwordEncoder.encode(registrationDto.getPassword());

        // Neuen Nutzer erstellen.
        User newUser = new User(
                registrationDto.getStudiengang(),
                registrationDto.getVerfuegbareTermine(),
                registrationDto.getName(),
                registrationDto.getEmail(),
                hashedPassword,
                Role.USER
        );

        // In der Datenbank speichern
        newUser.setId(UUID.randomUUID());
        User savedUser = userRepository.save(newUser);

        // Als sicheres DTO ohne Passwort zurückgeben
        return mapToResponse(savedUser);
    }

    private UserResponseDTO mapToResponse(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setStudiengang(user.getStudiengang());
        response.setVerfuegbareTermine(user.getVerfuegbareTermine());
        return response;
    }
}