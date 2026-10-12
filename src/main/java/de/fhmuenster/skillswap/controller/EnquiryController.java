package de.fhmuenster.skillswap.controller;

import de.fhmuenster.skillswap.dto.EnquiryCreateRequestDTO;
import de.fhmuenster.skillswap.dto.EnquiryResponseDTO;
import de.fhmuenster.skillswap.service.EnquiryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enquiries")
public class EnquiryController
{

    private final EnquiryService enquiryService;

    public EnquiryController(EnquiryService enquiryService){
        this.enquiryService = enquiryService;
    }

    @GetMapping
    public ResponseEntity<List<EnquiryResponseDTO>> getRequests() {
        List<EnquiryResponseDTO> requests = enquiryService.getAllEnquiries();
        return ResponseEntity.ok(requests);
    }

    @PostMapping
    public ResponseEntity<EnquiryResponseDTO> createRequest(@Valid @RequestBody EnquiryCreateRequestDTO request) {
        EnquiryResponseDTO createdRequest = enquiryService.createEnquiry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnquiry(@PathVariable UUID id) {
        boolean isDeleted = enquiryService.deleteEnquiry(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
