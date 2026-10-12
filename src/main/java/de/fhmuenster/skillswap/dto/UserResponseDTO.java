package de.fhmuenster.skillswap.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserResponseDTO
{

    private  UUID id;
    private String name;
    private String email;
    private String studiengang;
    private List<LocalDateTime> verfuegbareTermine;

    public List<LocalDateTime> getVerfuegbareTermine() {
        return verfuegbareTermine;
    }

    public void setVerfuegbareTermine(List<LocalDateTime> verfuegbareTermine) {
        this.verfuegbareTermine = verfuegbareTermine;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudiengang() {
        return studiengang;
    }

    public void setStudiengang(String studiengang) {
        this.studiengang = studiengang;
    }
}
