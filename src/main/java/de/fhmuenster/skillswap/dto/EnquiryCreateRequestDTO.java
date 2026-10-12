package de.fhmuenster.skillswap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class EnquiryCreateRequestDTO
{

    @NotBlank(message = "Titel darf nicht leer sein")
    private String titel;

    @NotBlank(message = "Beschreibung darf nicht leer sein")
    @Size(max = 500)
    private String beschreibung;

    @NotNull(message = "User ID darf nicht fehlen")
    private UUID userId;

    //leer weil domain expert sagt das ist best practice
    public EnquiryCreateRequestDTO() {
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}