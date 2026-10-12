package de.fhmuenster.skillswap.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class OfferResponseDTO
{

    private UUID id;
    private String titel;
    private String beschreibung;
    private LocalDateTime erstelltAm;
    private UUID userId; //_(oder verschachteltes UserResponse-Objekt)_

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }

    public void setErstelltAm(LocalDateTime erstelltAm) {
        this.erstelltAm = erstelltAm;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
