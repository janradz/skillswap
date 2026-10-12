package de.fhmuenster.skillswap.controller;

import de.fhmuenster.skillswap.dto.BookmarkResponseDTO;
import de.fhmuenster.skillswap.service.BookmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookmarks")
public class BookmarkController
{
    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService)
    {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping("/offers")
    public ResponseEntity<List<BookmarkResponseDTO>> getBookmarkedOffers()
    {
        List<BookmarkResponseDTO> bookmarks = bookmarkService.getBookmarkList();

        if (bookmarks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(bookmarks);
    }

    @DeleteMapping("/offers/{id}")
    public ResponseEntity<List<BookmarkResponseDTO>> deleteOfferBookmark(@PathVariable UUID id)
    {
        List<BookmarkResponseDTO> bookmarks = bookmarkService.removeOfferFromBookmarks(id);
        return ResponseEntity.ok(bookmarks);
    }

    @PostMapping("/offers/{offerID}")
    public ResponseEntity<List<BookmarkResponseDTO>> addOfferBookmark(@PathVariable UUID offerID)
    {
        List<BookmarkResponseDTO> bookmarks = bookmarkService.addOfferToBookmarks(offerID);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookmarks);
    }
}