package de.fhmuenster.skillswap.service;

import de.fhmuenster.skillswap.dto.BookmarkResponseDTO;
import de.fhmuenster.skillswap.model.Offer;
import de.fhmuenster.skillswap.repository.OfferRepository;
import de.fhmuenster.skillswap.model.UserBookmarks;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookmarkService
{
    private UserBookmarks bookmarks;
    private OfferRepository offerRepository;

    public BookmarkService(UserBookmarks bookmarks, OfferRepository offerRepository)
    {
        this.bookmarks = bookmarks;
        this.offerRepository = offerRepository;
    }

    public List<BookmarkResponseDTO> addOfferToBookmarks(UUID id)
    {
        bookmarks.addOffer(id);
        return getBookmarkedOffers(offerRepository);
    }

    public List<BookmarkResponseDTO> removeOfferFromBookmarks(UUID id)
    {
        bookmarks.removeOffer(id);
        return getBookmarkedOffers(offerRepository);
    }

    public List<BookmarkResponseDTO> getBookmarkedOffers(OfferRepository repository)
    {
        return bookmarks.getBookmarkedOfferIds().stream()
                .map(repository::findById)
                .flatMap(Optional::stream)  // Wandelt Optional<Offer> → Stream<Offer>
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BookmarkResponseDTO> getBookmarkList()
    {
        return getBookmarkedOffers(offerRepository);
    }

    private BookmarkResponseDTO mapToDTO(Offer offer) {
        BookmarkResponseDTO response = new BookmarkResponseDTO();
        response.setId(offer.getId());
        response.setTitel(offer.getTitel());
        response.setBeschreibung(offer.getBeschreibung());
        response.setErstelltAm(offer.getErstelltAm());
        response.setUser(offer.getUser());

        return response;
    }
}
