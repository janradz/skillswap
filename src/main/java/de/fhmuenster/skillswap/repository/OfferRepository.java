package de.fhmuenster.skillswap.repository;

import de.fhmuenster.skillswap.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {
    // Spring Data JPA erkennt den Methodennamen und generiert automatisch die Query:
    // "Lösche alle Offers, bei denen die ID des verknüpften Users mit der übergebenen ID übereinstimmt"
    void deleteByUserId(UUID userId);
}