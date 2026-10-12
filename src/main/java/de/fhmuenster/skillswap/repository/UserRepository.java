package de.fhmuenster.skillswap.repository;

import de.fhmuenster.skillswap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Standardmethoden wie save(), findAll(), findById(), deleteById() sind hier automatisch vorhanden

    Optional<User> findUserByName(String Name);
}