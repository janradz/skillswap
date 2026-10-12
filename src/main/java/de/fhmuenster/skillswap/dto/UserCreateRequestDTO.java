package de.fhmuenster.skillswap.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public class UserCreateRequestDTO
{
    @NotBlank(message = "Name darf nicht leer sein")
    private String name;

    @NotBlank (message = "Email darf nicht leer sein")
    @Email
    private  String email;
    @NotBlank (message = "Studiengang darf nicht leer sein")
    private  String studiengang;
    @NotBlank
    private String password;

    //platzhalter
    private  List<LocalDateTime> verfuegbareTermine;


    public UserCreateRequestDTO(){


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

    public List<LocalDateTime> getVerfuegbareTermine() {
        return verfuegbareTermine;
    }

    public void setVerfuegbareTermine(List<LocalDateTime> verfuegbareTermine) {
        this.verfuegbareTermine = verfuegbareTermine;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
