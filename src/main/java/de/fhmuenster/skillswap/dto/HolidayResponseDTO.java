package de.fhmuenster.skillswap.dto;
import java.time.LocalDate;

public class HolidayResponseDTO
{

        private LocalDate date;
        private String localName;

        public HolidayResponseDTO() {
        }


        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public String getLocalName() {
            return localName;
        }

        public void setLocalName(String localName) {
            this.localName = localName;
        }
}
