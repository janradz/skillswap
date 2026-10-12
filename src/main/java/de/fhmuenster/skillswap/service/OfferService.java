package de.fhmuenster.skillswap.service;

import de.fhmuenster.skillswap.dto.OfferCreateRequestDTO;
import de.fhmuenster.skillswap.dto.OfferResponseDTO;
import de.fhmuenster.skillswap.model.Offer;
import de.fhmuenster.skillswap.model.User;
import de.fhmuenster.skillswap.repository.OfferRepository;
import de.fhmuenster.skillswap.model.UserBookmarks;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OfferService
{

    private final OfferRepository offerRepository;
    private final UserService userService;
    private final UserBookmarks bookmarks;


    // OfferRepository und UserService über den Konstruktor injiziert
    public OfferService(OfferRepository offerRepository, UserService userService, UserBookmarks bookmarks)
    {
        this.offerRepository = offerRepository;
        this.userService = userService;
        this.bookmarks = bookmarks;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'GUEST')")
    public List<OfferResponseDTO> getAllOffers()
    {
        return offerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public OfferResponseDTO createOffer(OfferCreateRequestDTO request)
    {
        User user = userService.findModelById(request.getUserId());

        // Mit dem User das Offer-Modell bauen
        Offer neuesOffer = new Offer(
                request.getTitel(),
                request.getBeschreibung(),
                user
        );
        Offer savedOffer = offerRepository.save(neuesOffer);
        return mapToResponse(savedOffer);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public boolean deleteOffer(UUID id) {
        if (offerRepository.existsById(id)) {
            offerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private OfferResponseDTO mapToResponse(Offer offer)
    {
        OfferResponseDTO response = new OfferResponseDTO();
        response.setId(offer.getId());
        response.setTitel(offer.getTitel());
        response.setBeschreibung(offer.getBeschreibung());
        response.setErstelltAm(offer.getErstelltAm());
        response.setUserId(offer.getUser().getId());
        return response;
    }

}