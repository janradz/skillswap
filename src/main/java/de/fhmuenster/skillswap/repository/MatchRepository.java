package de.fhmuenster.skillswap.repository;

import de.fhmuenster.skillswap.model.Enquiry;
import de.fhmuenster.skillswap.model.Match;
import de.fhmuenster.skillswap.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {

    // Prüft, ob es für diese Kombination aus Angebot und Gesuch schon ein Match gibt, um Duplikate zu verhindern
    boolean existsByOfferAndEnquiry(Offer offer, Enquiry enquiry);
}