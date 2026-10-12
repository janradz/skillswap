package de.fhmuenster.skillswap.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String titel;
    private String beschreibung;
    private LocalDateTime erstelltAm;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected Offer() {}

    public Offer(String titel, String beschreibung, User user) {
        this.titel = titel;
        this.erstelltAm = LocalDateTime.now();
        this.beschreibung = beschreibung;
        this.user = user;
    }

    public UUID getId() { return id; }
    public String getTitel() { return titel; }
    public void setTitel(String titel) { this.titel = titel; }
    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getErstelltAm() { return erstelltAm; }
}