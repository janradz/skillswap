package de.fhmuenster.skillswap.repository;

import de.fhmuenster.skillswap.model.Enquiry;
import de.fhmuenster.skillswap.model.Match;
import de.fhmuenster.skillswap.model.Offer;
import de.fhmuenster.skillswap.model.Role;
import de.fhmuenster.skillswap.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RepositoryIntegrationTest {

    // TestEntityManager ist ein Helfer von @DataJpaTest,
    // um Daten direkt über JPA in die DB zu schreiben, ohne die Repositories zu nutzen.
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Test
    void testDeleteByUserId() {
        // --- 1. Arrange: Testdaten in die Datenbank schreiben ---

        // WICHTIG: User muss zuerst gespeichert werden, da Offer einen Foreign Key auf User hat!
        User testUser = new User("Informatik", new ArrayList<>(), "TestUser", "test@example.invalid", "hash", Role.USER);
        testUser.setId(UUID.randomUUID()); // <-- DAS HINZUFÜGEN

        testUser = entityManager.persist(testUser);

        Offer testOffer = new Offer("Mathe 1", "Biete Nachhilfe", testUser);
        entityManager.persist(testOffer);

        entityManager.flush();

        // Überprüfen, ob das Offer wirklich gespeichert wurde
        assertEquals(1, offerRepository.findAll().size());

        // --- 2. Act: Unsere Custom-Repository-Methode aufrufen ---
        offerRepository.deleteByUserId(testUser.getId());

        // --- 3. Assert: Überprüfen, ob es geklappt hat ---
        assertEquals(0, offerRepository.findAll().size(), "Das Offer sollte gelöscht worden sein");
    }

    @Test
    void testExistsByOfferAndEnquiry() {
        User anbieter = new User("Informatik", new ArrayList<>(), "Anbieter", "a@example.invalid", "hash", Role.USER);
        anbieter.setId(UUID.randomUUID());

        User sucher = new User("BWL", new ArrayList<>(), "Sucher", "s@example.invalid", "hash", Role.USER);
        sucher.setId(UUID.randomUUID());

        anbieter = entityManager.persist(anbieter);
        sucher = entityManager.persist(sucher);

        Offer offer = new Offer("Mathe", "Biete Mathe", anbieter);
        Enquiry enquiry = new Enquiry(sucher, "Suche Mathe", "Mathe");

        offer = entityManager.persist(offer);
        enquiry = entityManager.persist(enquiry);

        // Ein Match in der Datenbank anlegen
        Match match = new Match(offer, enquiry);
        match.setMatchscore(80);
        entityManager.persist(match);
        entityManager.flush();

        // --- 2. Act & Assert: Prüfen ob das Match gefunden wird ---

        boolean exists = matchRepository.existsByOfferAndEnquiry(offer, enquiry);
        assertTrue(exists, "Die Methode sollte true zurückgeben, da das Match existiert");

        // --- 3. Gegenprobe: Ein Enquiry ohne Match ---

        Enquiry otherEnquiry = new Enquiry(sucher, "Physik", "Physik");
        otherEnquiry = entityManager.persist(otherEnquiry);
        entityManager.flush();

        boolean existsNot = matchRepository.existsByOfferAndEnquiry(offer, otherEnquiry);
        assertFalse(existsNot, "Die Methode sollte false zurückgeben, da es diese Kombination nicht gibt");
    }
}