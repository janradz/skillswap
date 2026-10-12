package de.fhmuenster.skillswap.service;

import de.fhmuenster.skillswap.api.APIClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(HolidayService.class)
@Import(APIClientConfig.class) // Lädt deine RestClient-Konfiguration (holidayApiClient)
class HolidayServiceIntegrationTest {

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private MockRestServiceServer mockServer; // Simuliert den echten Nager-Date-Server

    @Test
    void findHolidayName_ShouldReturnName_WhenDateIsHoliday() {
        // Arrange: Ein Datum, das tatsächlich ein Feiertag sein soll
        LocalDate neujahr = LocalDate.of(2024, 1, 1);

        // Simuliere die exakte JSON-Antwort, die die echte API liefern würde
        String mockJsonResponse = """
            [
              {
                "date": "2024-01-01",
                "localName": "Neujahr",
                "name": "New Year's Day",
                "countryCode": "DE"
              },
              {
                "date": "2024-03-29",
                "localName": "Karfreitag",
                "name": "Good Friday",
                "countryCode": "DE"
              }
            ]
            """;

        // Wir sagen dem Mock-Server: Wenn eine GET-Anfrage an diese URL kommt, gib das JSON zurück
        mockServer.expect(requestTo("https://date.nager.at/api/v3/PublicHolidays/2024/DE"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockJsonResponse, MediaType.APPLICATION_JSON));

        // Act: Wir rufen die ganz normale Methode im Service auf
        Optional<String> result = holidayService.findHolidayName(neujahr);

        // Assert: Prüfen, ob das JSON richtig gemappt und "Neujahr" gefunden wurde
        assertTrue(result.isPresent());
        assertEquals("Neujahr", result.get());

        // Verifiziert, dass der HTTP-Request wirklich genauso vom RestClient abgesetzt wurde
        mockServer.verify();
    }

    @Test
    void findHolidayName_ShouldReturnEmpty_WhenDateIsNotHoliday() {
        // Arrange: Ein normaler Tag, der NICHT in der API-Antwort steht
        LocalDate normalerTag = LocalDate.of(2024, 1, 2);

        String mockJsonResponse = """
            [
              {
                "date": "2024-01-01",
                "localName": "Neujahr",
                "name": "New Year's Day",
                "countryCode": "DE"
              }
            ]
            """;

        mockServer.expect(requestTo("https://date.nager.at/api/v3/PublicHolidays/2024/DE"))
                .andRespond(withSuccess(mockJsonResponse, MediaType.APPLICATION_JSON));

        // Act
        Optional<String> result = holidayService.findHolidayName(normalerTag);

        // Assert: Das Optional muss leer sein, da das Datum nicht in der JSON-Liste ist
        assertTrue(result.isEmpty());
        mockServer.verify();
    }

    @Test
    void findHolidayName_ShouldReturnEmpty_WhenApiReturnsError() {
        // Arrange: Die API ist z.B. down und wirft einen 500 Error
        LocalDate neujahr = LocalDate.of(2024, 1, 1);

        mockServer.expect(requestTo("https://date.nager.at/api/v3/PublicHolidays/2024/DE"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        // Act: Der Aufruf
        Optional<String> result = holidayService.findHolidayName(neujahr);

        // Assert
        assertTrue(result.isEmpty());
        mockServer.verify();
    }
}