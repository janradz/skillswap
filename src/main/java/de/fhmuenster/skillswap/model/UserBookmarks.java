package de.fhmuenster.skillswap.model;


import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.*;

@Component
@SessionScope
public class UserBookmarks {

    private Set<UUID> bookmarks = new HashSet<>(); //Offer IDs

    public void addOffer(UUID offerId) {
        bookmarks.add(offerId);
    }

    public void removeOffer(UUID offerId) {
        bookmarks.remove(offerId);
    }

    public Set<UUID> getBookmarkedOfferIds() {
        return bookmarks;
    }
}