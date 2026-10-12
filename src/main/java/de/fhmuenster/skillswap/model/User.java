package de.fhmuenster.skillswap.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String studiengang;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_verfuegbare_termine", joinColumns = @JoinColumn(name = "user_id"))
    private List<LocalDateTime> verfuegbareTermine;

    // JPA benötigt zwingend einen parameterlosen Konstruktor
    protected User() {}

    public User(String studiengang, List<LocalDateTime> verfuegbareTermine, String name, String email, String passwordHash, Role role)
    {
        // ID wird nun von der Datenbank/JPA generiert, daher aus dem Konstruktor entfernt
        this.studiengang = studiengang;
        this.verfuegbareTermine = verfuegbareTermine;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<LocalDateTime> getVerfuegbareTermine() { return verfuegbareTermine; }
    public void setVerfuegbareTermine(List<LocalDateTime> verfuegbareTermine) { this.verfuegbareTermine = verfuegbareTermine; }
    public String getStudiengang() { return studiengang; }
    public void setStudiengang(String studiengang) { this.studiengang = studiengang; }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }

    public Role getRole()
    {
        return role;
    }

    public void setRole(Role role)
    {
        this.role = role;
    }
}