package de.fhmuenster.skillswap.controller;

import de.fhmuenster.skillswap.dto.OfferCreateRequestDTO;
import de.fhmuenster.skillswap.dto.OfferResponseDTO;
import de.fhmuenster.skillswap.service.OfferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public ResponseEntity<List<OfferResponseDTO>> getOffers(Authentication auth) {
        boolean isGuest = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_GUEST"));
        List<OfferResponseDTO> offers = offerService.getAllOffers();
        if (isGuest) {
            offers = offers.stream().limit(3).collect(Collectors.toList());
        }
        return ResponseEntity.ok(offers);
    }

    @PostMapping
    public ResponseEntity<OfferResponseDTO> createOffer(@Valid @RequestBody OfferCreateRequestDTO request) {
        OfferResponseDTO createdOffer = offerService.createOffer(request);
        // Gibt 201 Created zurück
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOffer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable UUID id) {
        boolean isDeleted = offerService.deleteOffer(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}