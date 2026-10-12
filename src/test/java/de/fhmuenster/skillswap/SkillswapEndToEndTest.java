package de.fhmuenster.skillswap;

import de.fhmuenster.skillswap.dto.OfferCreateRequestDTO;
import de.fhmuenster.skillswap.dto.OfferResponseDTO;
import de.fhmuenster.skillswap.dto.UserCreateRequestDTO;
import de.fhmuenster.skillswap.dto.UserResponseDTO;
import de.fhmuenster.skillswap.repository.EnquiryRepository;
import de.fhmuenster.skillswap.repository.MatchRepository;
import de.fhmuenster.skillswap.repository.OfferRepository;
import de.fhmuenster.skillswap.repository.UserRepository;
import de.fhmuenster.skillswap.service.HolidayService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

// Fährt die gesamte Anwendung auf einem zufälligen freien Port hoch
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class SkillswapEndToEndTest {

    @Autowired
    private TestRestTemplate restTemplate; // Unser Werkzeug für echte HTTP-Anfragen über das Netzwerk

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private EnquiryRepository enquiryRepository;

    @Autowired
    private MatchRepository matchRepository;

    @MockitoBean
    private HolidayService holidayService; // Isolierung der externen API

    @MockitoBean
    private JwtDecoder jwtDecoder; // MUSS gemockt werden, da wir keinen echten Keycloak Server haben

    @BeforeEach
    void setUp() {
        // Sicherstellen, dass die Registrierung nicht wegen Feiertagen fehlschlägt
        Mockito.when(holidayService.findHolidayName(any())).thenReturn(Optional.empty());
    }

    @AfterEach
    void tearDown() {
        // Datenbank nach dem Test komplett leeren, um andere Tests nicht zu beeinflussen
        matchRepository.deleteAll();
        offerRepository.deleteAll();
        enquiryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void fullUserLifecycle_ShouldRegister_ThenCreateOffer_ThenRetrieveItFromDatabase() {

        // --- SCHRITT 1: Registrierung eines neuen Users ---
        UserCreateRequestDTO registerRequest = new UserCreateRequestDTO();
        registerRequest.setName("e2euser");
        registerRequest.setEmail("e2e@example.invalid");
        registerRequest.setStudiengang("Informatik");
        registerRequest.setPassword("securePassword123");
        registerRequest.setVerfuegbareTermine(new ArrayList<>());

        // POST-Anfrage an den echten Server senden (Register ist permitAll)
        ResponseEntity<UserResponseDTO> registerResponse = restTemplate.postForEntity(
                "/users/register",
                registerRequest,
                UserResponseDTO.class
        );

        // Prüfen, ob der User erfolgreich in der echten DB angelegt wurde
        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());
        UUID generatedUserId = registerResponse.getBody().getId();
        assertNotNull(generatedUserId);


        // --- SCHRITT 2: Angebot erstellen mit Authentifizierung (JWT) ---

        // 1. JWT faken und Mock konfigurieren
        String fakeToken = "valid-test-token";
        Jwt mockJwt = Jwt.withTokenValue(fakeToken)
                .header("alg", "none")
                // Der KeycloakUserSyncListener benötigt diese Claims:
                .claim("sub", generatedUserId.toString()) // Wichtig: ID muss zum eben erstellten DB-User passen!
                .claim("preferred_username", "e2euser")
                .claim("email", "e2e@example.invalid")
                // Deine SecurityConfig wandelt den "roles"-Claim um
                .claim("roles", List.of("USER"))
                .build();

        Mockito.when(jwtDecoder.decode(fakeToken)).thenReturn(mockJwt);

        // 2. Request Payload bauen
        OfferCreateRequestDTO offerRequest = new OfferCreateRequestDTO();
        offerRequest.setTitel("E2E Test-Nachhilfe");
        offerRequest.setBeschreibung("Wir testen das Zusammenspiel aller Schichten.");
        offerRequest.setUserId(generatedUserId);

        // 3. HTTP Header mit Bearer Token setzen
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(fakeToken); // Hier geben wir den JWT mit!
        HttpEntity<OfferCreateRequestDTO> offerHttpEntity = new HttpEntity<>(offerRequest, headers);

        // 4. Echten HTTP POST Request abschicken
        ResponseEntity<OfferResponseDTO> offerResponse = restTemplate.exchange(
                "/offers",
                HttpMethod.POST,
                offerHttpEntity,
                OfferResponseDTO.class
        );

        // Prüfen, ob das Angebot erfolgreich verarbeitet und verknüpft wurde
        assertEquals(HttpStatus.CREATED, offerResponse.getStatusCode());
        assertNotNull(offerResponse.getBody());
        assertEquals("E2E Test-Nachhilfe", offerResponse.getBody().getTitel());


        // --- SCHRITT 3: Angebote abrufen und DB-Persistenz prüfen ---

        // Wir nutzen wieder die Header mit dem Bearer-Token
        HttpEntity<Void> getHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<OfferResponseDTO[]> getAllOffersResponse = restTemplate.exchange(
                "/offers",
                HttpMethod.GET,
                getHttpEntity,
                OfferResponseDTO[].class
        );

        // Prüfen, ob der HTTP-Status 200 ist und das Array Daten enthält
        assertEquals(HttpStatus.OK, getAllOffersResponse.getStatusCode());
        assertNotNull(getAllOffersResponse.getBody());
        assertTrue(getAllOffersResponse.getBody().length > 0);

        // Wir wandern durch das echte Datenbank-Ergebnis und suchen unser erstelltes Angebot
        boolean offerFoundInDb = false;
        for (OfferResponseDTO offer : getAllOffersResponse.getBody()) {
            if (offer.getTitel().equals("E2E Test-Nachhilfe") && offer.getUserId().equals(generatedUserId)) {
                offerFoundInDb = true;
                break;
            }
        }

        // Wenn die Kette (Controller -> Service -> Repository -> DB) funktioniert, muss das Angebot da sein!
        assertTrue(offerFoundInDb, "Das erstellte Angebot wurde nicht in der Datenbank gefunden.");
    }
}