package de.fhmuenster.skillswap.repository;

import de.fhmuenster.skillswap.model.Enquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, UUID> {
    void deleteByUserId(UUID userId);
}