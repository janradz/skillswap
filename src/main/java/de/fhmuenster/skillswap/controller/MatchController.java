package de.fhmuenster.skillswap.controller;

import de.fhmuenster.skillswap.dto.MatchResponseDTO;
import de.fhmuenster.skillswap.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<List<MatchResponseDTO>> getMatches() {
        List<MatchResponseDTO> matches = matchService.getAllMatches();
        return ResponseEntity.ok(matches);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable UUID id) {
        boolean isDeleted = matchService.deleteMatch(id);

        if (isDeleted) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}