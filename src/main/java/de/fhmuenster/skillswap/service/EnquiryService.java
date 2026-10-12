package de.fhmuenster.skillswap.service;

import de.fhmuenster.skillswap.dto.EnquiryCreateRequestDTO;
import de.fhmuenster.skillswap.dto.EnquiryResponseDTO;
import de.fhmuenster.skillswap.model.Enquiry;
import de.fhmuenster.skillswap.model.User;
import de.fhmuenster.skillswap.repository.EnquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EnquiryService
{

    private final EnquiryRepository enquiryRepository;
    private final UserService userService;

    public EnquiryService(EnquiryRepository enquiryRepository, UserService userService) {
        this.enquiryRepository = enquiryRepository;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'GUEST')")
    public List<EnquiryResponseDTO> getAllEnquiries() {
        return enquiryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public EnquiryResponseDTO createEnquiry(EnquiryCreateRequestDTO enquiryCreateRequestDTO) {
        // User-Klasse über UserService laden
        User echterUser = userService.findModelById(enquiryCreateRequestDTO.getUserId());

        // Request Instanz mit User-Klasse erstellen
        Enquiry neuesGesuch = new Enquiry(
                echterUser,
                enquiryCreateRequestDTO.getBeschreibung(),
                enquiryCreateRequestDTO.getTitel()
        );

        // 3. Im Repository speichern
        Enquiry savedRequest = enquiryRepository.save(neuesGesuch);

        // 4. Als DTO zurückgeben
        return mapToResponse(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public boolean deleteEnquiry(UUID id) {
        if (enquiryRepository.existsById(id)) {
            enquiryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Hilfsmethode für Mapping von Model zu DTO
    private EnquiryResponseDTO mapToResponse(Enquiry enquiry) {
        EnquiryResponseDTO response = new EnquiryResponseDTO();
        response.setId(enquiry.getId());
        response.setTitel(enquiry.getTitel());
        response.setBeschreibung(enquiry.getBeschreibung());
        response.setErstelltAm(enquiry.getErstelltAm());
        response.setUserId(enquiry.getUser().getId());
        return response;
    }
}