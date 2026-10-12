package de.fhmuenster.skillswap.service;

import de.fhmuenster.skillswap.dto.MatchResponseDTO;
import de.fhmuenster.skillswap.model.Enquiry;
import de.fhmuenster.skillswap.model.Match;
import de.fhmuenster.skillswap.model.Offer;
import de.fhmuenster.skillswap.repository.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<MatchResponseDTO> getAllMatches() {
        return matchRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Diese Methode wird nicht mehr vom Controller aufgerufen,
    // sondern später intern vom Matching-Algorithmus!
    @Transactional
    public Match createAutomaticMatch(Offer offer, Enquiry enquiry, int calculatedScore, LocalDateTime vereinbarterTermin) {
        Match neuesMatch = new Match(offer, enquiry);
        neuesMatch.setMatchscore(calculatedScore); // Score setzen, der von der KI berechnet wurde
        neuesMatch.setVereinbarterTermin(vereinbarterTermin); // Den Termin speichern!

        return matchRepository.save(neuesMatch);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteMatch(UUID id) {
        if (matchRepository.existsById(id)) {
            matchRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private MatchResponseDTO mapToResponse(Match match) {
        MatchResponseDTO response = new MatchResponseDTO();
        response.setId(match.getId());
        response.setMatchscore(match.getMatchscore());
        response.setStatus(match.getStatus());
        response.setVereinbarterTermin(match.getVereinbarterTermin());

        if (match.getOffer() != null) {
            response.setOfferId(match.getOffer().getId());
        }

        if (match.getInquiry() != null) {
            response.setEnquiryId(match.getInquiry().getId());
        }

        return response;
    }
}