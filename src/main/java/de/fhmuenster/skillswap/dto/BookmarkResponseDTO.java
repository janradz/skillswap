package de.fhmuenster.skillswap.dto;

import de.fhmuenster.skillswap.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class BookmarkResponseDTO
{
    private UUID id;
    private String titel;
    private String beschreibung;
    private LocalDateTime erstelltAm;
    private User user;

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getTitel()
    {
        return titel;
    }

    public void setTitel(String titel)
    {
        this.titel = titel;
    }

    public String getBeschreibung()
    {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung)
    {
        this.beschreibung = beschreibung;
    }

    public LocalDateTime getErstelltAm()
    {
        return erstelltAm;
    }

    public void setErstelltAm(LocalDateTime erstelltAm)
    {
        this.erstelltAm = erstelltAm;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}
