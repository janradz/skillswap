package de.fhmuenster.skillswap.dto;

import de.fhmuenster.skillswap.model.MatchStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class MatchResponseDTO {

    private UUID id;
    private int matchscore;
    private MatchStatus status;
    private UUID offerId;
    private UUID enquiryId;
    private LocalDateTime vereinbarterTermin;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getMatchscore() {
        return matchscore;
    }

    public void setMatchscore(int matchscore) {
        this.matchscore = matchscore;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public UUID getOfferId() {
        return offerId;
    }

    public void setOfferId(UUID offerId) {
        this.offerId = offerId;
    }

    public UUID getEnquiryId() {
        return enquiryId;
    }

    public void setEnquiryId(UUID enquiryId) {
        this.enquiryId = enquiryId;
    }

    public LocalDateTime getVereinbarterTermin() {
        return vereinbarterTermin;
    }

    public void setVereinbarterTermin(LocalDateTime vereinbarterTermin) {
        this.vereinbarterTermin = vereinbarterTermin;
    }
}