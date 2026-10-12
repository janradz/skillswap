package de.fhmuenster.skillswap.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "matches") // "match" kann in einigen DB-Systemen auch problematisch sein
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int matchscore;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private Offer offer;

    @ManyToOne
    @JoinColumn(name = "enquiry_id")
    private Enquiry enquiry;

    private LocalDateTime vereinbarterTermin;

    protected Match() {}

    public Match(Offer offer, Enquiry enquiry) {
        this.status = MatchStatus.PENDING;
        this.offer = offer;
        this.enquiry = enquiry;
    }

    public UUID getId() { return id; }
    public int getMatchscore() { return matchscore; }
    public void setMatchscore(int matchscore) { this.matchscore = matchscore; }
    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }
    public Offer getOffer() { return offer; }
    public void setOffer(Offer offer) { this.offer = offer; }
    public Enquiry getInquiry() { return enquiry; }
    public void setInquiry(Enquiry enquiry) { this.enquiry = enquiry; }
    public LocalDateTime getVereinbarterTermin() { return vereinbarterTermin; }
    public void setVereinbarterTermin(LocalDateTime vereinbarterTermin) { this.vereinbarterTermin = vereinbarterTermin; }
}