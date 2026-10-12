package de.fhmuenster.skillswap.service;


import de.fhmuenster.skillswap.dto.HolidayResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class HolidayService {

    private final RestClient restClient;


    //baue restClient auf im konstruktor
    public HolidayService(RestClient holidayApiClient) {
        this.restClient = holidayApiClient;
    }


    //business logic
    public Optional<String> findHolidayName(LocalDate date) {
        int year = date.getYear();
        try {
            // versuche feiertag zu ermitteln
            //spring ersetzt {year} durch jahr
            //jackson wandelt das resultat in ein HolidayResponse objekt um
                HolidayResponseDTO[] holidays = restClient.get()
                    .uri("/PublicHolidays/{year}/DE", year)
                    .retrieve()
                    .body(HolidayResponseDTO[].class);

            if (holidays == null) {
                return Optional.empty();
            }

            //iterier durch alle feiertage nach wunschdatum
            for (HolidayResponseDTO holiday : holidays) {
                if (date.equals(holiday.getDate())) {
                    return Optional.ofNullable(holiday.getLocalName());
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            //falls internet weg oder api down
            //erstmal behandeln als gäbe es keinen feiertag und fehler logging
            System.err.println("Fehler beim Abrufen der Feiertags-API: " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean isHoliday(LocalDate date) {
        return findHolidayName(date).isPresent();
    }

    public String getHoliday(LocalDate date){
        return findHolidayName(date).orElse(null);
    }
}